package es.us.lsi.dad;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.print.Doc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestServerGPS extends AbstractVerticle {
	
	private Map<Integer, Gps> gps = new HashMap<Integer, Gps>();
	private Gson gson;

	public void start(Promise<Void> startFuture) {
		// Creating some synthetic data
		createSomeData(25);

		// Instantiating a Gson serialize object using specific date format
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

		// Defining the router object
		Router router = Router.router(vertx);

		// Handling any server startup result
		vertx.createHttpServer().requestHandler(router::handle).listen(8080, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});

		// Defining URI paths for each method in RESTful interface, including body
		// handling by /api/gps* or /api/gps/*
		router.route("/api/gps*").handler(BodyHandler.create());
		router.get("/api/gps").handler(this::getAllWithParams);
		router.get("/api/gps/:id").handler(this::getOne);
		router.post("/api/gps").handler(this::addOne);
		router.delete("/api/gps/:id").handler(this::deleteOne);
		router.put("/api/gps/:id").handler(this::putOne);
	}

	@SuppressWarnings("unused")
	private void getAll(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(gps.values()));
	}

	private void getAllWithParams(RoutingContext routingContext) {
		final String lat = routingContext.queryParams().contains("lat") ? 
				routingContext.queryParam("lat").get(0) : null;
		final String lon = routingContext.queryParams().contains("lon") ? 
				routingContext.queryParam("lon").get(0) : null;
		final String dir = routingContext.queryParams().contains("dir") ? 
				routingContext.queryParam("dir").get(0) : null;
		
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(gps.values().stream().filter(elem -> {
					boolean res = true;
					res = res && lat != null ? elem.getLat().equals(Double.parseDouble(lat)) : true;
					res = res && lon != null ? elem.getLong().equals(Double.parseDouble(lon)) : true;
					res = res && dir != null ? elem.getDir().equals(Integer.parseInt(dir)) : true;
					return res;
				}).collect(Collectors.toList())));
	}

	private void getOne(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("id"));
		if (gps.containsKey(id)) {
			Gps ds = gps.get(id);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(gson.toJson(ds));
		} else {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(204)
					.end();
		}
	}

	private void addOne(RoutingContext routingContext) {
		final Gps gpss = gson.fromJson(routingContext.getBodyAsString(), Gps.class);
		gps.put(gpss.getId(), gpss);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(gpss));
	}

	private void deleteOne(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("id"));
		if (gps.containsKey(id)) {
			Gps gpss = gps.get(id);
			gps.remove(id);
			routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
					.end(gson.toJson(gpss));
		} else {
			routingContext.response().setStatusCode(204).putHeader("content-type", "application/json; charset=utf-8")
					.end();
		}
	}

	private void putOne(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("id"));
		Gps ds = gps.get(id);
		final Gps element = gson.fromJson(routingContext.getBodyAsString(), Gps.class);
		
		ds.setLat(element.getLat());
		ds.setLong(element.getLong());
		ds.setDir(element.getDir());
		ds.setVel(element.getVel());
		ds.setAlt(element.getAlt());
		gps.put(ds.getId(), ds);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(element));
	}

	private void createSomeData(int number) {
		Random rnd = new Random();
		
		IntStream.range(0, number).forEach(elem -> {
			int id = rnd.nextInt();
			gps.put(id, new Gps(id, rnd.nextDouble(),  rnd.nextDouble(), rnd.nextInt(), rnd.nextDouble(), rnd.nextDouble()));
		});
	System.out.println(gps);
	}

}