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

public class ApiRest extends AbstractVerticle{

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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		
		//Esto funciona ↓
		
		mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(1883, "192.168.43.253", s -> { //cambiar ip a la del ordenador que haga de servidor

			mqttClient.subscribe("topic_2", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
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
				}catch (JsonSyntaxException e) {
					System.out.println("    No es un Gps. ");
				}
			});
			mqttClient.publish("topic_1", Buffer.buffer("Ejemplo"), MqttQoS.AT_LEAST_ONCE, false, false);

		});

	
		//getAll();		
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void getAll() {
		mySqlClient.query("SELECT * FROM dad_db_avion.gps;").execute(res -> {
			if (res.succeeded()) {
				// Get the result set
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size());
				List<Gps> result = new ArrayList<>();
				for (Row elem : resultSet) {
					result.add(new Gps(elem.getInteger("id_Gps"), elem.getInteger("id_Fly"),
							elem.getDouble("lat"), elem.getDouble("lon"),
							elem.getInteger("dir"), elem.getDouble("vel"), elem.getDouble("alt"), elem.getLong("time")));
				}
				System.out.println(gson.toJson(result));
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
						List<Gps> result = new ArrayList<>();
						for (Row elem : resultSet) {
							result.add(new Gps(elem.getInteger("id_Gps"), elem.getInteger("id_Fly"),
											elem.getDouble("lat"), elem.getDouble("lon"),
											elem.getInteger("dir"), elem.getDouble("vel"), elem.getDouble("alt"), elem.getLong("time")));
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
	
	





	
	private void postGps(RoutingContext routingContext){
		final Gps gps = gson.fromJson(routingContext.getBodyAsString(), Gps.class);	
		//gps.setTime(System.currentTimeMillis());
		mySqlClient.preparedQuery("INSERT INTO dad_db_avion.gps (id_Fly, lat, lon, dir, vel, alt, time) VALUES (?,?,?,?,?,?,?)")
				.execute(Tuple.of(gps.getId_Fly(), gps.getLat(), gps.getLon(), gps.getDir(), gps.getVel(), gps.getAlt(), gps.getTime()) 	
				,handler -> {	
				if (handler.succeeded()) {
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(gson.toJson(gps));
					System.out.println(gson.toJson(gps));
					
					mqttClient.publish("topic_1", Buffer.buffer("0"), MqttQoS.AT_LEAST_ONCE, false, false);

					
					//AÃ±adir consulta sql
				}else {
					routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
					.end((gson.toJson(handler.cause())));
					System.out.println("Error"+handler.cause().getLocalizedMessage());
				}
			});
	}
	
}

