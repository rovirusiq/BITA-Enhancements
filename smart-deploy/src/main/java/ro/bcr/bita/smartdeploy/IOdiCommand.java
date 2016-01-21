package ro.bcr.bita.smartdeploy;

import oracle.odi.core.OdiInstance;

public interface IOdiCommand {

	public void execute(OdiInstance odiInstance);
}
