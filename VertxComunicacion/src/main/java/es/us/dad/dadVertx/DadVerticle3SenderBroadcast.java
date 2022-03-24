package es.us.dad.dadVertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;

public class DadVerticle3SenderBroadcast extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startFuture) {
		EventBus eventBus = getVertx().eventBus();
		getVertx().setPeriodic(2000, _id -> {
			eventBus.publish("mensaje-broadcast", "Soy Broadcast, ¿alguien me escucha?");
		});
	}

	@Override
	public void stop(Promise<Void> stopFuture) throws Exception {
		getVertx().undeploy(DadVerticle2Consumer.class.getName());
		super.stop(stopFuture);
	}
}


