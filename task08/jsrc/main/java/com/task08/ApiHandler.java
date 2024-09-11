package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.Architecture;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.task08.OpenMeteoClient;


@LambdaHandler(
    lambdaName = "api_handler",
	roleName = "api_handler-role",
	layers = {"open-meteo-layer"},
	isPublishVersion = false,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaLayer(
    layerName = "open-meteo-layer",
    libraries = {"lib/httpclient-4.5.13.jar", "lib/httpcore-4.4.14.jar"}, 
    runtime = DeploymentRuntime.JAVA11,
    architectures = {Architecture.ARM64},
    artifactExtension = ArtifactExtension.ZIP
)
@LambdaUrlConfig(
    authType = AuthType.NONE,
    invokeMode = InvokeMode.BUFFERED
)
public class ApiHandler implements RequestHandler<Object, Map<String, Object>> {

    @Override
	public Map<String, Object> handleRequest(Object request, Context context) {
		OpenMeteoClient client = new OpenMeteoClient();
		Map<String, Object> resultMap = new HashMap<>();
		
		try {
			// Fetch weather forecast from the OpenMeteo API
			String forecast = client.fetchWeather();
			resultMap.put("statusCode", 200);
			resultMap.put("body", forecast);
		} catch (Exception e) {
			// Log the exception and return an error response
			context.getLogger().log("Error fetching weather forecast: " + e.getMessage());
			resultMap.put("statusCode", 500);
			resultMap.put("body", "Error fetching weather forecast");
		}
	
		return resultMap;
	}
}