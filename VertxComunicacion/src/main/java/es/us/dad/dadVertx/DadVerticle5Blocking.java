package es.us.dad.dadVertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class DadVerticle5Blocking extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startFuture) {
		// Manera incorrecta de ejecutar código bloqueante
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
		}
	}

	@Override
	public void stop(Promise<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}
}
