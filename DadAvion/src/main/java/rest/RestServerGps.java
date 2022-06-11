package rest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import clases.Gps;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
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



public class RestServerGps extends AbstractVerticle {

	private Gson gson;

	MySQLPool mySqlClient;

	MqttClient mqttClient;
	public void start(Promise<Void> startPromise) {
		// Creating some synthetic data
		//createSomeData(25);
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("covid_database").setUser("root").setPassword("iissi$root");

		//getOne(null);

		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
		mySqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);

		// Instantiating a Gson serialize object using specific date format
		//gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

		// Defining the router object
		Router router = Router.router(vertx);

		// Handling any server startup result
		vertx.createHttpServer().requestHandler(router::handle).listen(8080, result -> {
			if (result.succeeded()) {
				startPromise.complete();
			} else {
				startPromise.fail(result.cause());
			}
		});

		// Defining URI paths for each method in RESTful interface, including body
		// handling by /api/users* or /api/users/*
		
		
		//////////////////////////////////////////////////////////////////////////////////////
		
		
		mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
        mqttClient.connect(1883, "192.168.43.253", s -> { // cambiar ip a la del ordenador que haga de servidor

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
                } catch (JsonSyntaxException e) {
                    System.out.println("    No es un Gps. ");
                }
            });
            mqttClient.publish("topic_1", Buffer.buffer("Ejemplo"), MqttQoS.AT_LEAST_ONCE, false, false);

        });

        // getAll();
    }
}