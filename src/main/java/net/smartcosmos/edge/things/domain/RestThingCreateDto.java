package net.smartcosmos.edge.things.domain;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.commons.lang.BooleanUtils;

/**
 * Data Transfer Object for REST request to create a new Thing.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Create a \"Thing\" in the Things Server.")
public class RestThingCreateDto {
    private static final int VERSION_1 = 1;
    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private int version = VERSION_1;

    private String type;

    @ApiModelProperty(notes = "The programmer provided URN for the Thing. This must be unique to the tenant. If not provided, it will be + " +
                              "generated. Size is database implementation dependent.", required = false)
    private String urn;

    @ApiModelProperty(notes = "Default: true.")
    private Boolean active = true;

    @Builder
    @ConstructorProperties({ "urn", "active", "type" })
    public RestThingCreateDto(String urn, String type, Boolean active) {
        this.urn = urn;
        this.type = type;
        this.active = BooleanUtils.toBoolean(active);
        this.version = VERSION_1;
    }
}
