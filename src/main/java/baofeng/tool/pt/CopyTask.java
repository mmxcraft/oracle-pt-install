package baofeng.tool.pt;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Copy;

/**
 * Created by baxue on 6/25/2014.
 */
public class CopyTask extends Copy {
    public CopyTask() {
        setProject(new Project());
        getProject().init();
        setTaskName("CopyTask");
        setTaskType("CopyTask");
        setOwningTarget(new Target());
    }
}