package net.smartcosmos.edge.things;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;

import net.smartcosmos.annotation.EnableSmartCosmosEvents;
import net.smartcosmos.annotation.EnableSmartCosmosExtension;
import net.smartcosmos.annotation.EnableSmartCosmosMonitoring;
import net.smartcosmos.annotation.EnableSmartCosmosSecurity;
import net.smartcosmos.edge.things.config.ThingsEdgeConfiguration;

@EnableSmartCosmosExtension
@EnableSmartCosmosEvents
@EnableSmartCosmosMonitoring
@EnableSmartCosmosSecurity
@EnableRetry
@EnableSwagger2
@Import(ThingsEdgeConfiguration.class)
public class ThingEdgeService {

    public static void main(String[] args) {

        new SpringApplicationBuilder(ThingEdgeService.class).web(true)
            .run(args);
    }
}
