package es.us.dad.dadVertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

public class DadVerticle2Sender extends AbstractVerticle {
	String verticleID = "";

	@Override
	public void start(Promise<Void> startFuture) {
		EventBus eventBus = getVertx().eventBus();
		getVertx().setPeriodic(4000, _id -> {
			eventBus.request("mensaje-punto-a-punto", "Soy Local,¿alguien me escucha?", reply -> {
				Message<Object> res = reply.result();
				verticleID = res.address();
				if (reply.succeeded()) {
					String replyMessage = (String) res.body();
					System.out.println("Respuesta recibida (" + res.address() + "): " + replyMessage + "\n\n\n");
				} else {
					System.out.println("No ha habido respuesta");
				}
			});
			
			eventBus.send("mensaje-punto-a-punto", "Esto es un mensaje sin respuesta");
		});
		
		startFuture.complete();
	}

	@Override
	public void stop(Promise<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}
}


