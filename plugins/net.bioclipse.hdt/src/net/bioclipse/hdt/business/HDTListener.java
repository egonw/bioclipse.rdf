package net.bioclipse.hdt.business;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.rdfhdt.hdt.listener.ProgressListener;

public class HDTListener implements ProgressListener {

	String filename;
	SubMonitor monitor;

	public HDTListener(String filename, IProgressMonitor monitor) {
		this.filename = filename;
		this.monitor = SubMonitor.convert(monitor, 100);
	}

	@Override
	public void notifyProgress(float percentageDone, String message) {
		System.out.println("Progress message: " + message);
		monitor.setTaskName(filename + ": " + message);
		// FIXME: percentage done does not return value between 0, 100
//		int workRemainging = 100-((int)percentageDone);
//		monitor.setWorkRemaining(workRemainging);
//		System.out.println("       remaining: " + workRemainging);
	}

}
