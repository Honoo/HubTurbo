package github.update;

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_MILESTONES;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.client.PagedRequest;

import com.google.gson.reflect.TypeToken;

import github.GitHubClientExtended;

public class MilestoneUpdateService extends UpdateService<Milestone>{


    public MilestoneUpdateService(GitHubClientExtended client, String milestonesETag){
        super(client, SEGMENT_MILESTONES, milestonesETag);
    }

    private Map<String, String> createUpdatedMilestonesParams(){
        Map<String, String> params = new HashMap<>();
        params.put("state", "all");
        return params;
    }

    @Override
    protected PagedRequest<Milestone> createUpdatedRequest(IRepositoryIdProvider repoId){
        PagedRequest<Milestone> request = super.createUpdatedRequest(repoId);
        request.setParams(createUpdatedMilestonesParams());
        request.setType(new TypeToken<Milestone>(){}.getType());
        request.setArrayType(new TypeToken<ArrayList<Milestone>>(){}.getType());
        return request;
    }
}
