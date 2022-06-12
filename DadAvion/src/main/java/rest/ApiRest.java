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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import clases.Airport;

public class ApiRest extends AbstractVerticle {

	private MySQLPool mySqlClient;
	Gson gson;
	private MqttClient mqttClient;

	@Override
	public void start(Promise<Void> startPromise) {
		gson = new Gson();

		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("dad_db_avion").setUser("root").setPassword("rootroot");
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);// numero maximo de conexiones

		mySqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);

		Router router = Router.router(vertx); // Permite canalizar las peticiones

		// Creacion de un servidor http, recibe por parametro el puerto, el resultado
		vertx.createHttpServer().requestHandler(router::handle).listen(8080, result -> {
			if (result.succeeded()) {
				startPromise.complete();
			} else {
				startPromise.fail(result.cause());
			}
		});

		// Definimos la rutas que se le pasan al servido http
		
		//Rutas de acceso para GPS ↓
		router.route("/api/*").handler(BodyHandler.create());
		router.get("/api/gps").handler(this::getAllWithConnectionGPS);
		router.get("/api/gps/:id").handler(this::getById_Gps);
		router.post("/api/gps").handler(this::postGps);
		
		// Rutas de acceso para Fly ↓
		router.get("/api/fly").handler(this::getAllWithConnectionFly);
		router.get("/api/fly/:id").handler(this::getById_Fly);
		router.put("/api/fly").handler(this::putFly);

		// Rutas de acceso para Airport ↓
		router.get("/api/airport").handler(this::getAllWithConnectionAirport);
		router.get("/api/airport/:id").handler(this::getById_Airport);
		router.put("/api/airport").handler(this::putAirport);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

		// Esto funciona ↓

		mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(1883, "192.168.43.253", s -> { // cambiar ip a la del ordenador que haga de servidor

			mqttClient.subscribe("Sus", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
				if (handler.succeeded()) {
					System.out.println("SuscripciÃ³n " + mqttClient.clientId());
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
				} catch (JsonSyntaxException e) {
					System.out.println("    No es un Gps. ");
				}
			});
			mqttClient.publish("topic_1", Buffer.buffer("Ejemplo"), MqttQoS.AT_LEAST_ONCE, false, false);

		});

	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void getAll(RoutingContext routingContext) {
		mySqlClient.query("SELECT * FROM dad_db_avion.gps;").execute(res -> {
			if (res.succeeded()) {
				// Get the result set
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size());
				List<Gps> result = new ArrayList<>();
				for (Row elem : resultSet) {
					result.add(new Gps(elem.getInteger("id_Gps"), elem.getInteger("id_Fly"), elem.getDouble("lat"),
							elem.getDouble("lon"), elem.getInteger("dir"), elem.getDouble("vel"), elem.getDouble("alt"),
							elem.getLong("time")));
				}
				System.out.println(gson.toJson(result));
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
	}
	
	// Peticiones del GPS ↓
	
	private void getAllWithConnectionGPS(RoutingContext routingContext) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("SELECT * FROM dad_db_avion.gps LIMIT 5;").execute(res -> {
					if (res.succeeded()) {
						// Get the result set
						RowSet<Row> resultSet = res.result();
						System.out.println(resultSet.size());
						List<Gps> result = new ArrayList<>();
						for (Row elem : resultSet) {
							result.add(new Gps(elem.getInteger("id_Gps"), elem.getInteger("id_Fly"),
									elem.getDouble("lat"), elem.getDouble("lon"), elem.getInteger("dir"),
									elem.getDouble("vel"), elem.getDouble("alt"), elem.getLong("time")));
						}
						System.out.println(gson.toJson(result));
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
		
		Integer id_Gps = Integer.parseInt(routingContext.request().getParam("id"));
		
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("SELECT * FROM dad_db_avion.gps where id_Gps = "+id_Gps+";").execute(res -> {
					if (res.succeeded()) {
						// Get the result set
						RowSet<Row> resultSet = res.result();
						System.out.println(resultSet.size());
						List<Gps> result = new ArrayList<>();
						for (Row elem : resultSet) {
							result.add(new Gps(elem.getInteger("id_Gps"), elem.getInteger("id_Fly"),
									elem.getDouble("lat"), elem.getDouble("lon"), elem.getInteger("dir"),
									elem.getDouble("vel"), elem.getDouble("alt"), elem.getLong("time")));
						}
						System.out.println(gson.toJson(result));
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

	private void postGps(RoutingContext routingContext) {
		final Gps gps = gson.fromJson(routingContext.getBodyAsString(), Gps.class);
		//gps.setTime(System.currentTimeMillis());
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery(
						"INSERT INTO dad_db_avion.gps (id_Fly, lat, lon, dir, vel, alt, time) VALUES (?,?,?,?,?,?,?)")
						.execute(Tuple.of(gps.getId_Fly(), gps.getLat(), gps.getLon(), gps.getDir(), gps.getVel(),
								gps.getAlt(), System.currentTimeMillis()), handler -> {
									if (handler.succeeded()) {
										routingContext.response().setStatusCode(200)
												.putHeader("content-type", "application/json").end(gson.toJson(gps));
										//System.out.println(gson.toJson(gps));

										connection.result().preparedQuery(
												"SELECT * FROM (SELECT id_Gps, id_Fly, lat, lon, dir, vel, alt, time, ROW_NUMBER() OVER(PARTITION BY id_Fly ORDER BY time DESC) rn FROM dad_db_avion.gps where ST_Distance_Sphere(point(?,?),point(lon,lat)) <= 4000 ) a WHERE rn = 1 and id_Fly != ?")
												.execute(Tuple.of(gps.getLon(), gps.getLat(), gps.getId_Fly()), res -> {
													if (res.result().size() > 0) {
														
														
														RowSet<Row> resultSet = res.result();
														System.out.println(resultSet.size());
														List<Gps> result = new ArrayList<>();
														for (Row elem : resultSet) {
															result.add(new Gps(elem.getInteger("id_Gps"), elem.getInteger("id_Fly"),
																	elem.getDouble("lat"), elem.getDouble("lon"), elem.getInteger("dir"),
																	elem.getDouble("vel"), elem.getDouble("alt"), elem.getLong("time")));
														}
														//System.out.println(gson.toJson(result));
														
														System.out.println(res.result().size());
														mqttClient.publish("topic_"+gps.getId_Fly(), Buffer.buffer("1"), MqttQoS.AT_LEAST_ONCE, false, false);

													}else {
														mqttClient.publish("topic_"+gps.getId_Fly(), Buffer.buffer("0"), MqttQoS.AT_LEAST_ONCE, false, false);

													}
													connection.result().close();
												});
									} else {
										routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end();
										connection.result().close();
									}
								});

			} else {
				routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end();
				connection.result().close();
			}
		});
	}
	
	// Peticiones Fly ↓
	
	private void getAllWithConnectionFly(RoutingContext routingContext) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("SELECT * FROM dad_db_avion.fly LIMIT 5;").execute(res -> {
					if (res.succeeded()) {
						// Get the result set
						RowSet<Row> resultSet = res.result();
						System.out.println(resultSet.size());
						List<Fly> result = new ArrayList<>();
						for (Row elem : resultSet) {
							result.add(new Fly(elem.getInteger("id_Fly"), elem.getInteger("id_AirportDest"), 
									elem.getInteger("id_AirportOrig"), elem.getString("plate"),  elem.getLong("time_Dep"), elem.getLong("time_Arr")));
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
		Integer id_Fly = Integer.parseInt(routingContext.request().getParam("id"));
		
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("SELECT * FROM dad_db_avion.fly where id_Fly = "+id_Fly+";").execute(res -> {
					if (res.succeeded()) {
						// Get the result set
						RowSet<Row> resultSet = res.result();
						System.out.println(resultSet.size());
						List<Fly> result = new ArrayList<>();
						for (Row elem : resultSet) {
							result.add(new Fly(elem.getInteger("id_Fly"), elem.getInteger("id_AirportDest"), 
									elem.getInteger("id_AirportOrig"), elem.getString("plate"),  elem.getLong("time_Dep"), elem.getLong("time_Arr")));
						}
						System.out.println(result.toString());
						routingContext.response().setStatusCode(200)
						.putHeader("content-type", "application/json").end(gson.toJson(result));
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
	
	private void putFly(RoutingContext routingContext) {

		final Fly fly = gson.fromJson(routingContext.getBodyAsString(), Fly.class);
		
		System.out.println(fly);
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("UPDATE dad_db_avion.fly SET id_AirportDest = ?, id_AirportOrig = ?, plate = ?, time_Dep = ?, time_Arr = ? WHERE id_Fly = "+fly.getId_Fly()+";")
						.execute(Tuple.of(fly.getId_AirportDest(), fly.getId_AirportOrig(), fly.getPlate(), fly.getTime_Dep(), fly.getTime_Arr()),
						handler -> {
							if (handler.succeeded()) {
								routingContext.response().setStatusCode(200)
								.putHeader("content-type", "application/json").end(gson.toJson(fly));
							} else {
								routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end();
								connection.result().close();
							}
						});
			}
		});
	}
	
	// Peticiones Airport ↓
	
	private void getAllWithConnectionAirport(RoutingContext routingContext) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("SELECT * FROM dad_db_avion.airport LIMIT 5;").execute(res -> {
					if (res.succeeded()) {
						// Get the result set
						RowSet<Row> resultSet = res.result();
						System.out.println(resultSet.size());
						List<Airport> result = new ArrayList<>();
						for (Row elem : resultSet) {
							result.add(new Airport(elem.getInteger("id_Airport"), elem.getString("name"), 
											 elem.getDouble("lat"), elem.getDouble("lon")));
						}
						System.out.println(result.toString());
						System.out.println(gson.toJson(result));
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
		Integer id_Airport = Integer.parseInt(routingContext.request().getParam("id"));
		
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("SELECT * FROM dad_db_avion.airport where id_Airport = "+id_Airport+";").execute(res -> {
					if (res.succeeded()) {
						// Get the result set
						RowSet<Row> resultSet = res.result();
						System.out.println(resultSet.size());
						List<Airport> result = new ArrayList<>();
						for (Row elem : resultSet) {
							result.add(new Airport(elem.getInteger("id_Airport"), elem.getString("name"), 
											 elem.getDouble("lat"), elem.getDouble("lon")));
						}
						System.out.println(result.toString());
						routingContext.response().setStatusCode(200)
						.putHeader("content-type", "application/json").end(gson.toJson(result));
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
	
	
	private void putAirport(RoutingContext routingContext) {

		final Airport airportMod = gson.fromJson(routingContext.getBodyAsString(), Airport.class);
		
		System.out.println(airportMod);
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("UPDATE dad_db_avion.airport SET name = ?, lat = ?, lon = ? WHERE id_Airport = ?;")
						.execute(Tuple.of(airportMod.getName(),airportMod.getLat(), airportMod.getLon(),airportMod.getId_Airport()),
						handler -> {
							if (handler.succeeded()) {
								routingContext.response().setStatusCode(200)
								.putHeader("content-type", "application/json").end(gson.toJson(airportMod));
							} else {
								routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end();
								connection.result().close();
							}
						});
			}
		});
	}
	
//	private void putAirport(RoutingContext routingContext) { //Actualiza un Aeropuerto
//		Integer id_Airport = Integer.parseInt(routingContext.request().getParam("id"));
//		Airport airport = Json.decodeValue(routingContext.getBodyAsString(), Airport.class);
//		mySqlClient.preparedQuery(
//				"UPDATE user SET name = ?, lat = ?, lon = ? WHERE id_Airport = "+id_Airport+";",
//				Tuple.of(airport.getName(), airport.getLat(), airport.getLon(),
//						routingContext.request().getParam("id_Airport")),
//				handler -> {
//					if (handler.succeeded()) {
//						routingContext.response().setStatusCode(200)
//						.putHeader("content-type", "application/json").end(gson.toJson(airport));
//					} else {
//						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
//								.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
//					}
//				});
//	}
	
}

