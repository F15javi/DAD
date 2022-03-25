package es.us.lsi.dad;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

public class RestClientGPS extends AbstractVerticle {

	public RestClientUtil restClientUtil;

	public void start(Promise<Void> startFuture) {
		WebClientOptions options = new WebClientOptions().setUserAgent("RestClientApp/2.0.2.1");
		options.setKeepAlive(false);
		restClientUtil = new RestClientUtil(WebClient.create(vertx, options));

		/* --------------- GET many request --------------- */

		Promise<Gps[]> resList = Promise.promise();
		resList.future().onComplete(complete -> {
			if (complete.succeeded()) {
				System.out.println("GetAll:");
				Stream.of(complete.result()).forEach(elem -> {
					System.out.println(elem.toString());
				});
			} else {
				System.out.println(complete.cause().toString());
			}
		});

		restClientUtil.getRequest(443, "https://6238e3ff00ed1dbc5ab885fb.mockapi.io", "api/v1/gps", 
				Gps[].class, resList);

		/* --------------- GET one request --------------- */

		Promise<Gps> res = Promise.promise();
		res.future().onComplete(complete -> {
			if (complete.succeeded()) {
				System.out.println("GetOne");
				System.out.println(complete.result().toString());
			} else {
				System.out.println(complete.cause().toString());
			}
		});

		restClientUtil.getRequest(443, "https://6238e3ff00ed1dbc5ab885fb.mockapi.io", "api/v1/gps/1", 
				Gps.class, res);

		/* --------------- GET request con parámetros--------------- */

		Promise<Gps> resWithParams = Promise.promise();
		resWithParams.future().onComplete(complete -> {
			if (complete.succeeded()) {
				System.out.println("GetOne With params");
				System.out.println(complete.result().toString());
			} else {
				System.out.println(complete.cause().toString());
			}
		});
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "3");
		params.put("otroparam", "123123");
		params.put("otroparam2", "hola");
		restClientUtil.getRequestWithParams(443, "https://6238e3ff00ed1dbc5ab885fb.mockapi.io", "api/v1/gps/1", 
				 Gps.class,
				resWithParams, params);

		/* --------------- POST request --------------- */

		Promise<Gps> resPost = Promise.promise();
		resPost.future().onComplete(complete -> {
			if (complete.succeeded()) {
				System.out.println("Post One");
				System.out.println(complete.result().toString());
			} else {
				System.out.println(complete.cause().toString());
			}
		});

		

		restClientUtil.postRequest(443, "https://6238e3ff00ed1dbc5ab885fb.mockapi.io", "api/v1/gps",
				new Gps(1, 42.551122, -20.145248, 120, 400.5, 8000.5),
				Gps.class, resPost);
		
		/* --------------- PUT request --------------- */

		Promise<Gps> resPut = Promise.promise();
		resPost.future().onComplete(complete -> {
			if (complete.succeeded()) {
				System.out.println("Put");
				System.out.println(complete.result().toString());
			} else {
				System.out.println(complete.cause().toString());
			}
		});

		

		restClientUtil.putRequest(443, "https://6238e3ff00ed1dbc5ab885fb.mockapi.io", "api/v1/gps/1",
				new Gps(1, 42.551122, -20.145248, 120, 400.5, 7000.5),
				Gps.class, resPut);

		/* --------------- REMOVE request --------------- */

		Promise<String> resDelete = Promise.promise();
		resDelete.future().onComplete(complete -> {
			if (complete.succeeded()) {
				System.out.println("Remove One");
				System.out.println(complete.result().toString());
			} else {
				System.out.println(complete.cause().toString());
			}
		}).onFailure(fail -> {
			System.out.println("Remove One");
			System.out.println(fail.toString());
		});

		restClientUtil.deleteRequest(443, "https://6238e3ff00ed1dbc5ab885fb.mockapi.io", "api/v1/gps/3",
				resDelete);
		
		
		/* --------------- LAUNCH local server --------------- */
		vertx.deployVerticle(RestServer.class.getName(), deploy -> {
			if (deploy.succeeded()) {
				System.out.println("Verticle deployed");
			}else {
				System.out.println("Error deploying verticle");
			}
		});

	}

}
