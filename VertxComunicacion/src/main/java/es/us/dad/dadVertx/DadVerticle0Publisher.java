package es.us.dad.dadVertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class DadVerticle0Publisher extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startFuture) {
		String name1 = DadVerticle2ConsumerBroadcastGPS.class.getName();
		getVertx().deployVerticle(name1, deployResult -> {
			if (deployResult.succeeded()) {
				System.out.println(name1 + " (" + deployResult.result() + ") ha sido desplegado correctamente");
			} else {
				deployResult.cause().printStackTrace();
			}
		});
		
		String name2 = DadVerticle2SenderBroadcastGPS.class.getName();
		getVertx().deployVerticle(name2, deployResult -> {
			if (deployResult.succeeded()) {
				System.out.println(name2 + " (" + deployResult.result() + ") ha sido desplegado correctamente");
			} else {
				deployResult.cause().printStackTrace();
			}
		});
	}

	@Override
	public void stop(Promise<Void> stopFuture) throws Exception {
		getVertx().undeploy(DadVerticle2ConsumerBroadcastGPS.class.getName());
		getVertx().undeploy(DadVerticle2SenderBroadcastGPS.class.getName());
		super.stop(stopFuture);
	}
}


