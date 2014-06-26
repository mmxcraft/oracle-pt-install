package baofeng.tool.pt;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Expand;

/**
 * Created by baxue on 6/25/2014.
 */
class ExpandTask extends Expand {
    public ExpandTask() {
        setProject(new Project());
        getProject().init();
        setTaskName("ExpandTask");
        setTaskType("ExpandTask");
        setOwningTarget(new Target());
    }
}
