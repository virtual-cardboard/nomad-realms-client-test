package loading;

import static loading.NomadRealmsLoadingInfo.SOURCE_PATH;

import context.visuals.lwjgl.Shader;
import context.visuals.lwjgl.ShaderType;
import engine.common.loader.loadtask.ShaderLoadTask;

public class NomadRealmsShaderLoadTask extends ShaderLoadTask {

	public NomadRealmsShaderLoadTask(ShaderType type, String sourceLocation) {
		super(type, SOURCE_PATH + sourceLocation);
	}

	public NomadRealmsShaderLoadTask(Shader shader, String sourceLocation) {
		super(shader, SOURCE_PATH + sourceLocation);
	}

}
