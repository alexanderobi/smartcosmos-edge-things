package net.smartcosmos.edge.things.domain.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@JsonIgnoreProperties({ "version" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestMetadataCreateResponseDto {

    private static final int VERSION_1 = 1;

    private final int version = VERSION_1;

    private String uri;

    private int code;

    private String message;
}
