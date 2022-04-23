package mqtt;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import clases.Gps;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

public class MqttClientVerticleGps extends AbstractVerticle{
	Gson gson;

	public void start(Promise<Void> startFuture) {
		gson = new Gson();
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

}
//[
// '{{repeat(5, 7)}}',
// {
//   id_Gps: '{{integer(0, 20)}}',
//   id_Fly: '{{integer(0, 20)}}',
//   lat: '{{floating(-180, 180, 8, "0.00000000")}}',
//   lon: '{{floating(-90, 90, 8, "0.00000000")}}',
//   dir: '{{integer(0, 359)}}',
//   vel: '{{floating(0, 2000, 2, "0.00")}}',
//   alt: '{{floating(0, 10000, 2, "0.00")}}',
//   hor: '{{integer(1587489279, 1713719679)}}'
// }
//]