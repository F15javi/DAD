package es.us.dad.dadVertx;

import com.google.gson.Gson;

import io.vertx.core.AbstractVerticle;

import io.vertx.core.Promise;

public class DadVerticle2ConsumerBroadcastGPS extends AbstractVerticle{
	
	public void start(Promise<Void> startFuture) {
		getVertx().eventBus().consumer("mensaje-punto-a-punto", message -> {
			String customMessage = (String) message.body();
			Gson gson = new Gson();
			Gps jsonMessage = gson.fromJson(customMessage, Gps.class);
			System.out.println("Mensaje recibido (" + message.address() + "): " + jsonMessage.toString());
			String replyMessage = "Sí, yo te he escuchado al mensaje \"" + message.body().toString() + "\"";
			message.reply(replyMessage);
		});
		startFuture.complete();
	}

	@Override
	public void stop(Promise<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}

 
}
