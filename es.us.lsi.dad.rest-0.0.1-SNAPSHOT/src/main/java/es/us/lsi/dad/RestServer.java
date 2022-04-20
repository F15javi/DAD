package es.us.lsi.dad;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestServer extends AbstractVerticle {

	private Map<Integer, SensorEntity> sensors = new HashMap<Integer, SensorEntity>();
	private Gson gson;

	public void start(Promise<Void> startFuture) {
		
		// Instantiating a Gson serialize object using specific date format
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

		// Defining the router object
		Router router = Router.router(vertx);

		// Handling any server startup result
		HttpServer httpServer = vertx.createHttpServer();
		httpServer.requestHandler(router::handle).listen(80, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});

		// Defining URI paths for each method in RESTful interface, including body
		// handling by /api/users* or /api/users/*
		router.route("/api*").handler(BodyHandler.create());

		router.get("/api/sensors").handler(this::getSensors);
		router.get("/api/sensors/:sensorid").handler(this::getSensorById);
		router.post("/api/sensors").handler(this::addOneSensor);
		router.delete("/api/sensors/:sensorid").handler(this::deleteOneSensor);
		router.put("/api/sensors/:sensorid").handler(this::putOneSensor);

		// Lanzar el nuevo verticle
		// RestServer2 server2 = new RestServer2();
		// server2.setHttpServer(httpServer);
		// vertx.deployVerticle(server2);

	}

	private void getSensors(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
				.end(gson.toJson(sensors.values()));
	}

	private void getSensorById(RoutingContext routingContext) {
		SensorEntity sensor = null;
		int param = Integer.parseInt(routingContext.request().getParam("sensorid"));
		if (sensors.containsKey(param)) {
			sensor = sensors.get(param);
			routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
					.end(gson.toJson(sensor));
		} else {
			routingContext.response().putHeader("content-type", "application/json").setStatusCode(300).end();
		}
	}
	
	private void addOneSensor(RoutingContext routingContext) {
		final SensorEntity sensor = gson.fromJson(routingContext.getBodyAsString(), SensorEntity.class);
		sensors.put(sensor.getIdSensor(), sensor);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(sensor));
	}

	private void deleteOneSensor(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("sensorid"));
		if (sensors.containsKey(id)) {
			SensorEntity sensor = sensors.get(id);
			sensors.remove(id);
			routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
					.end(gson.toJson(sensor));
		} else {
			routingContext.response().setStatusCode(204).putHeader("content-type", "application/json; charset=utf-8")
					.end();
		}
	}

	private void putOneSensor(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("sensorid"));
		SensorEntity ds = sensors.get(id);
		final SensorEntity element = gson.fromJson(routingContext.getBodyAsString(), SensorEntity.class);
		ds.setData(element.getData());
		ds.setIdSensor(element.getIdSensor());
		ds.setSensor(element.getSensor());
		ds.setTime(element.getTime());
		sensors.put(ds.getIdSensor(), ds);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(element));
	}


}
