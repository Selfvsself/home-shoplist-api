package com.shoplist.api.shoplistapireciever.controller;

import com.shoplist.api.shoplistapireciever.model.Product;
import com.shoplist.api.shoplistapireciever.model.TextMessageDTO;
import com.shoplist.api.shoplistapireciever.repository.ProductRepository;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
public class WebSocketTextController {

    private final SimpMessagingTemplate template;
    private final ProductRepository productRepository;

    public WebSocketTextController(SimpMessagingTemplate template,
                                   ProductRepository productRepository) {
        this.template = template;
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProduct(KeycloakPrincipal<KeycloakSecurityContext> principal) {
        String userId = principal.getKeycloakSecurityContext().getToken().getSubject();
        if (userId.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
        List<Product> result = productRepository.getAllProductForUser(UUID.fromString(userId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TextMessageDTO> sendMessage(
            @RequestBody TextMessageDTO textMessageDTO,
            KeycloakPrincipal<KeycloakSecurityContext> principal) {
        String userId = principal.getKeycloakSecurityContext().getToken().getSubject();
        if (userId.isEmpty()) {
            TextMessageDTO result = new TextMessageDTO(TextMessageDTO.Type.ERROR.getValue(), textMessageDTO.getPayload());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        Product product = productRepository.addProduct(textMessageDTO.getPayload(), UUID.fromString(userId));
        TextMessageDTO result = new TextMessageDTO(TextMessageDTO.Type.ADD.getValue(), product);
        template.convertAndSend(String.format("/topic/message/%s", userId), result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TextMessageDTO> updateMessage(
            @PathVariable String id,
            @RequestBody TextMessageDTO textMessageDTO,
            KeycloakPrincipal<KeycloakSecurityContext> principal) {
        String userId = principal.getKeycloakSecurityContext().getToken().getSubject();
        if (userId.isEmpty()) {
            TextMessageDTO result = new TextMessageDTO(TextMessageDTO.Type.ERROR.getValue(), textMessageDTO.getPayload());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        Product receivedProduct = textMessageDTO.getPayload();
        Optional<Product> optionalProduct = productRepository.getProductById(UUID.fromString(id), UUID.fromString(userId));
        if (optionalProduct.isPresent()) {
            Product oldProduct = optionalProduct.get();
            if (!receivedProduct.getId().equals(oldProduct.getId())) {
                TextMessageDTO result = new TextMessageDTO(TextMessageDTO.Type.ERROR.getValue(), textMessageDTO.getPayload());
                return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
            }
            if (!receivedProduct.getOwner().equals(oldProduct.getOwner())) {
                TextMessageDTO result = new TextMessageDTO(TextMessageDTO.Type.ERROR.getValue(), textMessageDTO.getPayload());
                return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
            }
            try {
                Product product = productRepository.updateProduct(receivedProduct, UUID.fromString(userId));
                TextMessageDTO result = new TextMessageDTO(TextMessageDTO.Type.UPDATE.getValue(), product);
                template.convertAndSend(String.format("/topic/message/%s", userId), result);
                return new ResponseEntity<>(result, HttpStatus.OK);
            } catch (NullPointerException e) {
                TextMessageDTO result = new TextMessageDTO(TextMessageDTO.Type.ERROR.getValue(), textMessageDTO.getPayload());
                return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
            }
        } else {
            TextMessageDTO result = new TextMessageDTO(TextMessageDTO.Type.ERROR.getValue(), textMessageDTO.getPayload());
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping
    public ResponseEntity<TextMessageDTO> deleteMessage(
            @RequestBody TextMessageDTO textMessageDTO,
            KeycloakPrincipal<KeycloakSecurityContext> principal) {
        String userId = principal.getKeycloakSecurityContext().getToken().getSubject();
        if (userId.isEmpty()) {
            TextMessageDTO result = new TextMessageDTO(TextMessageDTO.Type.ERROR.getValue(), textMessageDTO.getPayload());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        try {
            Product product = productRepository.removeProduct(textMessageDTO.getPayload().getId(), UUID.fromString(userId));
            TextMessageDTO result = new TextMessageDTO(TextMessageDTO.Type.DELETE.getValue(), product);
            template.convertAndSend(String.format("/topic/message/%s", userId), result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NullPointerException e) {
            TextMessageDTO result = new TextMessageDTO(TextMessageDTO.Type.ERROR.getValue(), textMessageDTO.getPayload());
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
    }
}
