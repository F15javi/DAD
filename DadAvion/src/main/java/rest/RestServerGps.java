package rest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import clases.Gps;


public class RestServerGps extends AbstractVerticle {

	private Map<Integer, Gps> gps = new HashMap<Integer, Gps>();
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
				// handling by /api/gps* or /api/gps/*
				router.route("/api/gps*").handler(BodyHandler.create());
				router.get("/api/gps").handler(this::getAllWithParams);
				router.get("/api/gps/:id").handler(this::getOne);
				router.post("/api/gps").handler(this::addOne);
//				router.delete("/api/gps/:id").handler(this::deleteOne);
//				router.put("/api/gps/:id").handler(this::putOne);
	}
	@SuppressWarnings("unused")
	private void getAll(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(gps.values()));
	}
	
	private void getAllWithParams(RoutingContext routingContext) {
		final String id_Fly = routingContext.queryParams().contains("id_Fly") ? 
				routingContext.queryParam("id_Fly").get(0) : null;
		final String lat = routingContext.queryParams().contains("lat") ? 
				routingContext.queryParam("lat").get(0) : null;
		final String lon = routingContext.queryParams().contains("lon") ? 
				routingContext.queryParam("lon").get(0) : null;
		final String dir = routingContext.queryParams().contains("dir") ? 
				routingContext.queryParam("dir").get(0) : null;
		final String vel = routingContext.queryParams().contains("vel") ? 
				routingContext.queryParam("vel").get(0) : null;
		final String alt = routingContext.queryParams().contains("alt") ? 
				routingContext.queryParam("alt").get(0) : null;
		final String time = routingContext.queryParams().contains("time") ? 
				routingContext.queryParam("time").get(0) : null;
		
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
		.end(gson.toJson(gps.values().stream().filter(elem -> {
			boolean res = true;
			res = res && dir != null ? elem.getId_Fly().equals(Integer.parseInt(id_Fly)) : true;
			res = res && lat != null ? elem.getLat().equals(Double.parseDouble(lat)) : true;
			res = res && lon != null ? elem.getLon().equals(Double.parseDouble(lon)) : true;
			res = res && dir != null ? elem.getDir().equals(Integer.parseInt(dir)) : true;
			res = res && dir != null ? elem.getVel().equals(Double.parseDouble(vel)) : true;
			res = res && dir != null ? elem.getAlt().equals(Double.parseDouble(alt)) : true;
			res = res && dir != null ? elem.getTime().equals(Long.parseLong(time)) : true;
			return res;
		}).collect(Collectors.toList())));
	}
	
	private void getOne(RoutingContext routingContext) {
		int id_Gps = Integer.parseInt(routingContext.request().getParam("id_Gps"));
		if (gps.containsKey(id_Gps)) {
			Gps ds = gps.get(id_Gps);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(gson.toJson(ds));
		} else {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(204)
					.end();
		}
	}
	
	private void addOne(RoutingContext routingContext) {
		final Gps gpss = gson.fromJson(routingContext.getBodyAsString(), Gps.class);
		gps.put(gpss.getId_Gps(), gpss);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(gpss));
	}
	
//	private void deleteOne(RoutingContext routingContext) {
//		int id_Gps = Integer.parseInt(routingContext.request().getParam("id_Gps"));
//		if (gps.containsKey(id_Gps)) {
//			Gps gpss = gps.get(id_Gps);
//			gps.remove(id_Gps);
//			routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
//					.end(gson.toJson(gpss));
//		} else {
//			routingContext.response().setStatusCode(204).putHeader("content-type", "application/json; charset=utf-8")
//					.end();
//		}
//	}
	
//	private void putOne(RoutingContext routingContext) {
//		int id_Gps = Integer.parseInt(routingContext.request().getParam("id_Gps"));
//		Gps ds = gps.get(id_Gps);
//		final Gps element = gson.fromJson(routingContext.getBodyAsString(), Gps.class);
//		
//		ds.setLat(element.getLat());
//		ds.setLon(element.getLon());
//		ds.setDir(element.getDir());
//		ds.setVel(element.getVel());
//		ds.setDir(element.getDir());
//		ds.setVel(element.getVel());
//		ds.setAlt(element.getAlt());
//		ds.setTime(element.getTime());
//		
//		gps.put(ds.getId_Gps(), ds);
//		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
//				.end(gson.toJson(element));
//	}

}
	


