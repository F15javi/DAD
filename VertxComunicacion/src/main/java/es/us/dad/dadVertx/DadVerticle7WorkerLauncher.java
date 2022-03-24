package es.us.dad.dadVertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;

public class DadVerticle7WorkerLauncher extends AbstractVerticle {
	@Override
	public void start(Promise<Void> startFuture) {
		// Otra manera, creado un Worker Verticle
		DeploymentOptions options = new DeploymentOptions().setWorker(true);
		vertx.deployVerticle(DadVerticle7Worker.class.getName(), options, res -> {
			if (res.succeeded()) {
				System.out.println("Código bloqueante ejecutado");
			} else {
				System.out.println("Error en la ejecución");
			}
		});
	}

	@Override
	public void stop(Promise<Void> stopFuture) throws Exception {
		vertx.undeploy(DadVerticle7WorkerLauncher.class.getName());
		super.stop(stopFuture);
	}
}


