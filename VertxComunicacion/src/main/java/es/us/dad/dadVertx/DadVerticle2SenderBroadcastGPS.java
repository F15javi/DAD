package es.us.dad.dadVertx;


import com.google.gson.Gson;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;


public class DadVerticle2SenderBroadcastGPS extends AbstractVerticle{
	String verticleID = "";

	@Override
	public void start(Promise<Void> startFuture) {
		EventBus eventBus = getVertx().eventBus();
		getVertx().setPeriodic(4000, _id -> {
			Gps gps = new Gps(41.65657788, 52.09097567, 120, 402.06, 8000);
			Gson gson = new Gson();
			eventBus.request("mensaje-punto-a-punto", gson.toJson(gps), reply -> {
				Message<Object> res = reply.result();
				verticleID = res.address();
				if (reply.succeeded()) {
					String replyMessage = (String) res.body();
					System.out.println("Respuesta recibida (" + res.address() + "): " + replyMessage + "\n\n\n");
				} else {
					System.out.println("No ha habido respuesta");
				}
			});
		});
		
		startFuture.complete();
	}

	@Override
	public void stop(Promise<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}
}
