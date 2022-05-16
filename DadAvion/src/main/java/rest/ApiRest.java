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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import clases.Airport;

public class ApiRest extends AbstractVerticle{

	private MySQLPool mySqlClient;
	Gson gson;
	private MqttClient mqtt_client;
	@Override
	public void start(Promise<Void> startPromise) {
		
		
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
		router.route("/api/gps*").handler(BodyHandler.create());
		router.get("/api/gps").handler(this::getAllWithConnectionGPS);
		router.get("/api/gps/:id").handler(this::getById_Gps);
		router.post("/api/gps").handler(this::postGps);
		
		
		router.route("api/fly*").handler(BodyHandler.create());
		router.get("/api/fly").handler(this::getAllWithConnectionFLY);
		router.get("/api/fy/:id").handler(this::getById_Fly);
		router.post("/api/fly").handler(this::postFly);
		
		router.route("api/airport*").handler(BodyHandler.create());
		router.get("/api/airport").handler(this::getAllWithConnectionAIRPORT);
		router.get("/api/airport/:id").handler(this::getById_Airport);
		router.post("/api/airport").handler(this::postAirport);
	 
		
		
		MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true).setUsername("admin").setPassword("admin"));
		mqttClient.connect(1883, "localhost", connection -> {
		
			if(connection.succeeded()) {
				System.out.println("Nombre del cliente: " + connection.result().code().name());
				
				//subscripcion
				mqtt_client.subscribe("topic_1", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
					if(handler.succeeded()) {
						System.out.println("Subscripcion realizada correctamente");
					}else {
						System.out.println("Fallo en la subscripcion");
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

		}else {
			System.out.println("Error en la conexión con el broker");
			}
		});
	
		getAll();
		
	}
	
	private void getAll() {
		mySqlClient.query("SELECT * FROM dad_db_avion.gps;").execute(res -> {
			if (res.succeeded()) {
				// Get the result set
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size());
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(JsonObject.mapFrom(new Gps(elem.getInteger("id_Gps"), elem.getInteger("id_Fly"),
							elem.getDouble("lat"), elem.getDouble("lon"),
							elem.getInteger("dir"), elem.getDouble("vel"), elem.getDouble("alt"), elem.getLong("time"))));
				}
				System.out.println(result.toString());
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}
	
	private void getAllWithConnectionGPS(RoutingContext routingContext) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("SELECT * FROM dad_db_avion.gps;").execute( res -> {
					if (res.succeeded()) {
						// Get the result set
						RowSet<Row> resultSet = res.result();
						System.out.println(resultSet.size());
						JsonArray result = new JsonArray();
						for (Row elem : resultSet) {
							result.add(JsonObject
									.mapFrom(new Gps(elem.getInteger("id_Gps"), elem.getInteger("id_Fly"),
											elem.getDouble("lat"), elem.getDouble("lon"),
											elem.getInteger("dir"), elem.getDouble("vel"), elem.getDouble("alt"), elem.getLong("time"))));
						}
						System.out.println(result.toString());
					} else {
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
					connection.result().close();
				});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}
	
	
	private void getAllWithConnectionFLY(RoutingContext routingContext) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("SELECT * FROM dad_db_avion.fly;").execute( res -> {
					if (res.succeeded()) {
						// Get the result set
						RowSet<Row> resultSet = res.result();
						System.out.println(resultSet.size());
						JsonArray result = new JsonArray();
						for (Row elem : resultSet) {
							result.add(JsonObject
									.mapFrom(new Fly(elem.getInteger("id_Fly"), elem.getInteger("id_AirporDest"), 
											elem.getInteger("id_AirporOrig"), elem.getString("plate"),  elem.getLong("time_Dep"), elem.getLong("time_Arr"))));
						}
						System.out.println(result.toString());
					} else {
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
					connection.result().close();
				});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}
	
	private void getAllWithConnectionAIRPORT(RoutingContext routingContext) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("SELECT * FROM dad_db_avion.airport;").execute( res -> {
					if (res.succeeded()) {
						// Get the result set
						RowSet<Row> resultSet = res.result();
						System.out.println(resultSet.size());
						JsonArray result = new JsonArray();
						for (Row elem : resultSet) {
							result.add(JsonObject
									.mapFrom(new Airport(elem.getInteger("id_Airport"), elem.getString("name"), 
											 elem.getDouble("lat"), elem.getDouble("lon"))));
						}
						System.out.println(result.toString());
					} else {
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
					connection.result().close();
				});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}
	
	private void getById_Gps(RoutingContext routingContext) {
		Integer id_Gps = Integer.parseInt(routingContext.request().getParam("id_Gps"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM dad_db_avion.gps WHERE id_Gps = '" + id_Gps + "'").execute(
						Tuple.of(id_Gps), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new Gps(elem.getInteger("id_Gps"), elem.getInteger("id_Fly"),
											elem.getDouble("lat"), elem.getDouble("lon"), elem.getInteger("dir"), elem.getDouble("vel"), 
											elem.getDouble("alt"), elem.getLong("time"))));
								}
								System.out.println(result.toString());
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
							}
							connection.result().close();
						});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}
	
	
	private void getById_Fly(RoutingContext routingContext) {
		Integer id_Fly = Integer.parseInt(routingContext.request().getParam("id_Fly"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM dad_db_avion.fly WHERE id_Fly = '" + id_Fly + "'").execute(
						Tuple.of(id_Fly), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new Fly(elem.getInteger("id_Fly"), elem.getInteger("id_AirporDest"), 
											elem.getInteger("id_AirporOrig"), elem.getString("plate"),  elem.getLong("time_Dep"), elem.getLong("time_Arr"))));
								}
								System.out.println(result.toString());
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
							}
							connection.result().close();
						});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}
	
	private void getById_Airport(RoutingContext routingContext) {
		Integer id_Airport = Integer.parseInt(routingContext.request().getParam("id_Airport"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM dad_db_avion.airport WHERE id_Airport = '" + id_Airport + "'").execute(
						Tuple.of(id_Airport), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject.mapFrom(new Airport(elem.getInteger("id_Airport"), elem.getString("name"), 
											 elem.getDouble("lat"), elem.getDouble("lon"))));
								}
								System.out.println(result.toString());
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
							}
							connection.result().close();
						});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}
	
	private void postGps(RoutingContext routingContext){
		final Gps gps = gson.fromJson(routingContext.getBodyAsString(), Gps.class);	
		mySqlClient.preparedQuery("INSERT INTO dad_db_avion.gps (id_Gps, id_Fly, lat, lon, dir, vel, alt, time) VALUES (?,?,?,?,?,?,?,?)", 	// no se que poner para que nos haga el post.
				Tuple.of(gps.getId_Gps(), gps.getId_Fly(), gps.getLat(), gps.getLon(), gps.getDir(), gps.getVel(), gps.getAlt(), gps.getTime()),handler -> {	
				if (handler.succeeded()) {
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end("Gps registrado");
					System.out.println(JsonObject.mapFrom(usuario).encodePrettily()+"\n Gps registrado");
				}else {
					routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
					.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
					System.out.println("Error"+handler.cause().getLocalizedMessage());
				}
			});
	}
		
		
		private void postFly(RoutingContext routingContext){
			final Fly fly = gson.fromJson(routingContext.getBodyAsString(), Fly.class);	
			mySqlClient.preparedQuery("INSERT INTO dad_db_avion.fly (id_Fly, id_AirportDest, id_AirportOrig, plate, time_Dep, time_Arr) VALUES (?,?,?,?,?,?)", 	// no se que poner para que nos haga el post.
					Tuple.of(fly.getId_Fly(), fly.getId_AirportDest(), fly.getId_AirportOrig(), fly.getPlate(), fly.getTime_Dep(), fly.getTime_Arr()),handler -> {	
					if (handler.succeeded()) {
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end("Vuelo registrado");
						System.out.println(JsonObject.mapFrom(usuario).encodePrettily()+"\n Vuelo registrado");
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
						System.out.println("Error"+handler.cause().getLocalizedMessage());
					}
				});
	}
		
		private void postAirport(RoutingContext routingContext){
			final Airport airport = gson.fromJson(routingContext.getBodyAsString(), Airport.class);	
			mySqlClient.preparedQuery("INSERT INTO dad_db_avion.airport (id_Airport, name, lat, lon) VALUES (?,?,?,?)", 	// no se que poner para que nos haga el post.
					Tuple.of(airport.getId_Airport(), airport.getName(), airport.getLat(), airport.getLon()),handler -> {	
					if (handler.succeeded()) {
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end("Aeropuerto registrado");
						System.out.println(JsonObject.mapFrom(usuario).encodePrettily()+"\n Aeropuerto registrado");
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
						System.out.println("Error"+handler.cause().getLocalizedMessage());
					}
				});
	}
	
	
	
	
	
}