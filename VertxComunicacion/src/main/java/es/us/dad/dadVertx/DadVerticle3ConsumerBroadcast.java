package es.us.dad.dadVertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class DadVerticle3ConsumerBroadcast extends AbstractVerticle {
	@Override
	public void start(Promise<Void> startFuture) {
		getVertx().eventBus().consumer("mensaje-broadcast", message -> {
			String customMessage = (String) message.body();
			System.out.println("Mensaje recibido: " + customMessage);
			//message.replyAndRequest("hola");
		});
	}

	@Override
	public void stop(Promise<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}
}


