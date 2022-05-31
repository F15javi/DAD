package rest;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import clases.Gps;
import clases.Fly;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import clases.Airport;

public class ApiRest extends AbstractVerticle{

	private MySQLPool mySqlClient;
	Gson gson;
	private MqttClient mqtt_client;
	@Override
	public void start(Promise<Void> startPromise) {
		gson = new Gson();

		
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("dad_db_avion").setUser("root").setPassword("rootroot");
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);// numero maximo de conexiones
		
		mySqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);
		
		Router router = Router.router(vertx); // Permite canalizar las peticiones
		router.route().handler(BodyHandler.create());
		
		//Creacion de un servidor http, recibe por parametro el puerto, el resultado
		vertx.createHttpServer().requestHandler(router::handle).listen(8080, result -> {
			if (result.succeeded()) {
				startPromise.complete();
			}else {
				startPromise.fail(result.cause());
			}
		});

	
	// Definimos la rutas que se le pasan al servido http
		router.route("/api/*").handler(BodyHandler.create());
//		router.get("/api/gps").handler(this::getAllWithConnectionGPS);
//		router.get("/api/gps/:id").handler(this::getById_Gps);
		router.post("/api/gps").handler(this::postGps);
//		
//		
//		router.get("/api/fly").handler(this::getAllWithConnectionFLY);
//		router.get("/api/fy/:id").handler(this::getById_Fly);
//		
//		router.get("/api/airport").handler(this::getAllWithConnectionAIRPORT);
//		router.get("/api/airport/:id").handler(this::getById_Airport);
	 
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		
		MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(1883, "localhost", s -> {

			mqttClient.subscribe("topic_2", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
				if (handler.succeeded()) {
					System.out.println("Suscripción " + mqttClient.clientId());
				}
			});

			mqttClient.publishHandler(handler -> {
				System.out.println("Mensaje recibido:");
				System.out.println("    Topic: " + handler.topicName().toString());
				System.out.println("    Id del mensaje: " + handler.messageId());
				System.out.println("    Contenido: " + handler.payload().toString());
				try {
				Gps g = gson.fromJson(handler.payload().toString(), Gps.class);
				System.out.println("    Gps: " + g.toString());
				}catch (JsonSyntaxException e) {
					System.out.println("    No es un Gps. ");
				}
			});
			mqttClient.publish("topic_1", Buffer.buffer("Ejemplo"), MqttQoS.AT_LEAST_ONCE, false, false);
		});

	
		
		
	}
	
//	private void getAll() {
//		mySqlClient.query("SELECT * FROM dad_db_avion.gps;").execute(res -> {
//			if (res.succeeded()) {
//				// Get the result set
//				RowSet<Row> resultSet = res.result();
//				System.out.println(resultSet.size());
//				List<Gps> result = new ArrayList<>();
//				for (Row elem : resultSet) {
//					result.add(new Gps(elem.getInteger("id_Gps"), elem.getInteger("id_Fly"),
//							elem.getDouble("lat"), elem.getDouble("lon"),
//							elem.getInteger("dir"), elem.getDouble("vel"), elem.getDouble("alt"), elem.getLong("time")));
//				}
//				System.out.println(gson.toJson(result));
//			} else {
//				System.out.println("Error: " + res.cause().getLocalizedMessage());
//			}
//		});
//	}
//	
//	private void getAllWithConnectionGPS(RoutingContext routingContext) {
//		mySqlClient.getConnection(connection -> {
//			if (connection.succeeded()) {
//				connection.result().query("SELECT * FROM dad_db_avion.gps;").execute( res -> {
//					if (res.succeeded()) {
//						// Get the result set
//						RowSet<Row> resultSet = res.result();
//						System.out.println(resultSet.size());
//						List<Gps> result = new ArrayList<>();
//						for (Row elem : resultSet) {
//							result.add(new Gps(elem.getInteger("id_Gps"), elem.getInteger("id_Fly"),
//											elem.getDouble("lat"), elem.getDouble("lon"),
//											elem.getInteger("dir"), elem.getDouble("vel"), elem.getDouble("alt"), elem.getLong("time")));
//						}
//						System.out.println(gson.toJson(result));
//					} else {
//						System.out.println("Error: " + res.cause().getLocalizedMessage());
//					}
//					connection.result().close();
//				});
//			} else {
//				System.out.println(connection.cause().toString());
//			}
//		});
//	}
	
	
//	private void getAllWithConnectionFLY(RoutingContext routingContext) {
//		mySqlClient.getConnection(connection -> {
//			if (connection.succeeded()) {
//				connection.result().query("SELECT * FROM dad_db_avion.fly;").execute( res -> {
//					if (res.succeeded()) {
//						// Get the result set
//						RowSet<Row> resultSet = res.result();
//						System.out.println(resultSet.size());
//						List<Fly> result = new ArrayList<>();
//						for (Row elem : resultSet) {
//							result.add(new Fly(elem.getInteger("id_Fly"), elem.getInteger("id_AirporDest"), 
//											elem.getInteger("id_AirporOrig"), elem.getString("plate"),  elem.getLong("time_Dep"), elem.getLong("time_Arr")));
//						}
//						System.out.println(gson.toJson(result));
//					} else {
//						System.out.println("Error: " + res.cause().getLocalizedMessage());
//					}
//					connection.result().close();
//				});
//			} else {
//				System.out.println(connection.cause().toString());
//			}
//		});
//	}
	
//	private void getAllWithConnectionAIRPORT(RoutingContext routingContext) {
//		mySqlClient.getConnection(connection -> {
//			if (connection.succeeded()) {
//				connection.result().query("SELECT * FROM dad_db_avion.airport;").execute( res -> {
//					if (res.succeeded()) {
//						// Get the result set
//						RowSet<Row> resultSet = res.result();
//						System.out.println(resultSet.size());
//						List<Airport> result = new ArrayList<>();
//						for (Row elem : resultSet) {
//							result.add(new Airport(elem.getInteger("id_Airport"), elem.getString("name"), 
//											 elem.getDouble("lat"), elem.getDouble("lon")));
//						}
//						System.out.println(gson.toJson(result));
//					} else {
//						System.out.println("Error: " + res.cause().getLocalizedMessage());
//					}
//					connection.result().close();
//				});
//			} else {
//				System.out.println(connection.cause().toString());
//			}
//		});
//	}
	
//	private void getById_Gps(RoutingContext routingContext) {
//		Integer id_Gps = Integer.parseInt(routingContext.request().getParam("id_Gps"));
//		mySqlClient.getConnection(connection -> {
//			if (connection.succeeded()) {
//				connection.result().preparedQuery("SELECT * FROM dad_db_avion.gps WHERE id_Gps = ?").execute(
//						Tuple.of(id_Gps), res -> {
//							if (res.succeeded()) {
//								// Get the result set
//								RowSet<Row> resultSet = res.result();
//								System.out.println(resultSet.size());
//								List<Gps> result = new ArrayList<>();
//								for (Row elem : resultSet) {
//									result.add(new Gps(elem.getInteger("id_Gps"), elem.getInteger("id_Fly"),
//											elem.getDouble("lat"), elem.getDouble("lon"), elem.getInteger("dir"), elem.getDouble("vel"), 
//											elem.getDouble("alt"), elem.getLong("time")));
//								}
//								System.out.println(gson.toJson(result));
//							} else {
//								System.out.println("Error: " + res.cause().getLocalizedMessage());
//							}
//							connection.result().close();
//						});
//			} else {
//				System.out.println(connection.cause().toString());
//			}
//		});
//	}
//	
	
//	private void getById_Fly(RoutingContext routingContext) {
//		Integer id_Fly = Integer.parseInt(routingContext.request().getParam("id_Fly"));
//		mySqlClient.getConnection(connection -> {
//			if (connection.succeeded()) {
//				connection.result().preparedQuery("SELECT * FROM dad_db_avion.fly WHERE id_Fly = ?").execute(
//						Tuple.of(id_Fly), res -> {
//							if (res.succeeded()) {
//								// Get the result set
//								RowSet<Row> resultSet = res.result();
//								System.out.println(resultSet.size());
//								List<Fly> result = new ArrayList<>();
//								for (Row elem : resultSet) {
//									result.add(new Fly(elem.getInteger("id_Fly"), elem.getInteger("id_AirporDest"), 
//											elem.getInteger("id_AirporOrig"), elem.getString("plate"),  elem.getLong("time_Dep"), elem.getLong("time_Arr")));
//								}
//								System.out.println(gson.toJson(result));
//							} else {
//								System.out.println("Error: " + res.cause().getLocalizedMessage());
//							}
//							connection.result().close();
//						});
//			} else {
//				System.out.println(connection.cause().toString());
//			}
//		});
//	}
	
//	private void getById_Airport(RoutingContext routingContext) {
//		Integer id_Airport = Integer.parseInt(routingContext.request().getParam("id_Airport"));
//		mySqlClient.getConnection(connection -> {
//			if (connection.succeeded()) {
//				connection.result().preparedQuery("SELECT * FROM dad_db_avion.airport WHERE id_Airport = ?").execute(
//						Tuple.of(id_Airport), res -> {
//							if (res.succeeded()) {
//								// Get the result set
//								RowSet<Row> resultSet = res.result();
//								System.out.println(resultSet.size());
//								List<Airport> result = new ArrayList<>();
//								for (Row elem : resultSet) {
//									result.add(new Airport(elem.getInteger("id_Airport"), elem.getString("name"), 
//											 elem.getDouble("lat"), elem.getDouble("lon")));
//								}
//								System.out.println(gson.toJson(result));
//							} else {
//								System.out.println("Error: " + res.cause().getLocalizedMessage());
//							}
//							connection.result().close();
//						});
//			} else {
//				System.out.println(connection.cause().toString());
//			}
//		});
//	}
	
	private void postGps(RoutingContext routingContext){
		final Gps gps = gson.fromJson(routingContext.getBodyAsString(), Gps.class);	
		mySqlClient.preparedQuery("INSERT INTO dad_db_avion.gps (id_Fly, lat, lon, dir, vel, alt, time) VALUES (?,?,?,?,?,?,?)")
				.execute(Tuple.of(gps.getId_Fly(), gps.getLat(), gps.getLon(), gps.getDir(), gps.getVel(), gps.getAlt(), gps.getTime()) 	
				,handler -> {	
				if (handler.succeeded()) {
					//routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					//.end(gson.toJson(gps));
					System.out.println(gson.toJson(gps));
					
					//Añadir consulta sql
				}else {
					routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
					.end((gson.toJson(handler.cause())));
					System.out.println("Error"+handler.cause().getLocalizedMessage());
				}
			});
	}
	
}

