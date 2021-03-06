package net.smartcosmos.edge.things.service.things;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import net.smartcosmos.edge.things.ThingEdgeService;
import net.smartcosmos.edge.things.domain.things.RestThingUpdate;
import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.test.config.RetryTestConfig;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import static net.smartcosmos.test.util.TestUtil.unwrapAndVerify;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = { ThingEdgeService.class, RetryTestConfig.class })
@ActiveProfiles("test")
public class UpdateThingRestServiceDefaultTest {

    /*
     * TODO: 07/11/16 Update test after upgrading to Spring Boot 1.4
     *
     * The currently used version of Spring Boot requires unwrapping the bean to verify @Retryable methods when mock() is used to create the mocked
     * bean. Otherwise a UnfinishedVerificationException would be thrown.
     *
     * Once Spring Boot is upgraded and @MockBean replaced mock(), unwrapAndVerify() can be replaced by Mockito.verify() again.
     *
     * (see https://github.com/spring-projects/spring-boot/issues/6828)
     */

    // @MockBean // requires at least Spring Boot 1.4.0-RELEASE
    @Autowired
    private UpdateThingRestService service;

    final ResponseEntity expectedResponse = ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT)
        .build();

    @Before
    public void setUp() {

        initMocks(this);

        when(service.update(anyString(), anyString(), any(RestThingUpdate.class), any(SmartCosmosUser.class)))
            .thenThrow(new RuntimeException("Remote Exception 1"))
            .thenThrow(new RuntimeException("Remote Exception 2"))
            .thenReturn(expectedResponse);
    }

    @After
    public void tearDown() {

        validateMockitoUsage();
        reset(service);
    }

    @Test
    public void thatApplicationContextLoads() {

        assertNotNull(service);
    }

    @Test
    public void thatGetThingByTypeAndUrnRetries() {

        final String type = "someType";
        final String urn = "someUrn";
        final RestThingUpdate body = mock(RestThingUpdate.class);
        final SmartCosmosUser user = mock(SmartCosmosUser.class);

        ResponseEntity response = service.update(type, urn, body, user);

        assertEquals(expectedResponse, response);
        unwrapAndVerify(service, times(3)).update(eq(type), eq(urn), eq(body), eq(user));
        verifyNoMoreInteractions(service);
    }
}
