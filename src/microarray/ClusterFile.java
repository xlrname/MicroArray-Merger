/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package microarray;

import java.io.File;

/**
 *
 * @author XLR
 */
public class ClusterFile {
    public File file;
    public String experiment;

    public ClusterFile(File file, String experiment) {
        this.file = file;
        this.experiment = experiment;
    }

    public String getExperiment() {
        return experiment;
    }

    public void setExperiment(String experiment) {
        this.experiment = experiment;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
    
}
