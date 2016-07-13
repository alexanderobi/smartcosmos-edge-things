package net.smartcosmos.edge.things.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import net.smartcosmos.edge.things.domain.RestBadRequestResponseDto;

/**
 * Utility class for building responses.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseBuilderUtility {

    public static ResponseEntity buildBadRequestResponse(int errorCode, String errorMessage) {

        return ResponseEntity.badRequest()
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(RestBadRequestResponseDto.builder()
                      .message(errorMessage)
                      .build());
    }

}
