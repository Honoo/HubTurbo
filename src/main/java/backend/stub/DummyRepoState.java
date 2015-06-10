package backend.stub;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.User;

import backend.IssueMetadata;
import backend.resource.TurboIssue;
import backend.resource.TurboLabel;
import backend.resource.TurboMilestone;
import backend.resource.TurboUser;
import github.IssueEventType;
import github.TurboIssueEvent;

public class DummyRepoState {

    private String dummyRepoId;

    private TreeMap<Integer, TurboIssue> issues = new TreeMap<>();
    private TreeMap<String, TurboLabel> labels = new TreeMap<>();
    private TreeMap<Integer, TurboMilestone> milestones = new TreeMap<>();
    private TreeMap<String, TurboUser> users = new TreeMap<>();

    private TreeMap<Integer, TurboIssue> updatedIssues = new TreeMap<>();
    private TreeMap<String, TurboLabel> updatedLabels = new TreeMap<>();
    private TreeMap<Integer, TurboMilestone> updatedMilestones = new TreeMap<>();
    private TreeMap<String, TurboUser> updatedUsers = new TreeMap<>();

    public DummyRepoState(String repoId) {
        this.dummyRepoId = repoId;

        for (int i = 0; i < 10; i++) {
            // Issue #7 is a PR
            TurboIssue dummyIssue = (i != 6) ? makeDummyIssue() : makeDummyPR();
            // All default issues are treated as if created a long time ago
            dummyIssue.setUpdatedAt(LocalDateTime.of(2000 + i, 1, 1, 0, 0));
            TurboLabel dummyLabel = makeDummyLabel();
            TurboMilestone dummyMilestone = makeDummyMilestone();
            TurboUser dummyUser = makeDummyUser();

            // Populate state with default objects
            issues.put(dummyIssue.getId(), dummyIssue);
            labels.put(dummyLabel.getActualName(), dummyLabel);
            milestones.put(dummyMilestone.getId(), dummyMilestone);
            users.put(dummyUser.getLoginName(), dummyUser);
        }

        // Issues #1-5 are assigned milestones 1-5 respectively
        for (int i = 1; i <= 5; i++) {
            issues.get(i).setMilestone(milestones.get(i));
        }
        // Odd issues are assigned label 1, even issues are assigned label 2
        for (int i = 1; i <= 10; i++) {
            issues.get(i).addLabel((i % 2 == 0) ? "Label 1" : "Label 2");
        }
        // We assign a colorful label to issue 10
        labels.put("Label 11", new TurboLabel(dummyRepoId, "ffa500", "Label 11"));
        issues.get(10).addLabel("Label 11");
        // Each user is assigned to his corresponding issue
        for (int i = 1; i <= 10; i++) {
            issues.get(i).setAssignee("User " + i);
        }
        // Then put down three comments for issue 10
        Comment dummyComment1 = new Comment();
        Comment dummyComment2 = new Comment();
        Comment dummyComment3 = new Comment();
        dummyComment1.setCreatedAt(new Date()); // Recently posted
        dummyComment2.setCreatedAt(new Date());
        dummyComment3.setCreatedAt(new Date(0)); // Posted very long ago
        dummyComment1.setUser(new User().setLogin("User 1"));
        dummyComment2.setUser(new User().setLogin("User 2"));
        dummyComment3.setUser(new User().setLogin("User 3"));
        Comment[] dummyComments = { dummyComment1, dummyComment2, dummyComment3 };
        issues.get(10).setMetadata(new IssueMetadata(
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList(dummyComments))
        ));
        issues.get(10).setCommentCount(3);
        issues.get(10).setUpdatedAt(LocalDateTime.now());
        // Close issue 6
        issues.get(6).setOpen(false);
    }

    protected ImmutableTriple<List<TurboIssue>, String, Date>
        getUpdatedIssues(String eTag, Date lastCheckTime) {

        String currETag = eTag;
        if (!updatedIssues.isEmpty() || eTag == null) currETag = UUID.randomUUID().toString();

        ImmutableTriple<List<TurboIssue>, String, Date> toReturn = new ImmutableTriple<>(
            new ArrayList<>(updatedIssues.values()), currETag, lastCheckTime);

        updatedIssues = new TreeMap<>();
        return toReturn;
    }

    protected ImmutablePair<List<TurboLabel>, String> getUpdatedLabels(String eTag) {
        String currETag = eTag;
        if (!updatedLabels.isEmpty() || eTag == null) currETag = UUID.randomUUID().toString();

        ImmutablePair<List<TurboLabel>, String> toReturn
            = new ImmutablePair<>(new ArrayList<>(updatedLabels.values()), currETag);

        updatedLabels = new TreeMap<>();
        return toReturn;
    }

    protected ImmutablePair<List<TurboMilestone>, String> getUpdatedMilestones(String eTag) {
        String currETag = eTag;
        if (!updatedMilestones.isEmpty() || eTag == null) currETag = UUID.randomUUID().toString();

        ImmutablePair<List<TurboMilestone>, String> toReturn
            = new ImmutablePair<>(new ArrayList<>(updatedMilestones.values()), currETag);

        updatedMilestones = new TreeMap<>();
        return toReturn;
    }

    protected ImmutablePair<List<TurboUser>, String> getUpdatedCollaborators(String eTag) {
        String currETag = eTag;
        if (!updatedUsers.isEmpty() || eTag == null) currETag = UUID.randomUUID().toString();

        ImmutablePair<List<TurboUser>, String> toReturn
            = new ImmutablePair<>(new ArrayList<>(updatedUsers.values()), currETag);

        updatedUsers = new TreeMap<>();
        return toReturn;
    }

    protected List<TurboIssue> getIssues() {
        return new ArrayList<>(issues.values());
    }

    protected List<TurboLabel> getLabels() {
        return new ArrayList<>(labels.values());
    }

    protected List<TurboMilestone> getMilestones() {
        return new ArrayList<>(milestones.values());
    }

    protected List<TurboUser> getCollaborators() {
        return new ArrayList<>(users.values());
    }

    private TurboIssue makeDummyIssue() {
        return new TurboIssue(dummyRepoId,
                issues.size() + 1,
                "Issue " + (issues.size() + 1),
                "User " + (issues.size() + 1),
                LocalDateTime.of(1999 + issues.size(), 1, 1, 0, 0),
                false);
    }

    private TurboIssue makeDummyPR() {
        return new TurboIssue(dummyRepoId,
                issues.size() + 1,
                "PR " + (issues.size() + 1),
                "User " + (issues.size() + 1),
                LocalDateTime.of(1999 + issues.size(), 1, 1, 0, 0),
                true);
    }

    private TurboLabel makeDummyLabel() {
        return new TurboLabel(dummyRepoId, "Label " + (labels.size() + 1));
    }

    private TurboMilestone makeDummyMilestone() {
        return new TurboMilestone(dummyRepoId, milestones.size() + 1, "Milestone " + (milestones.size() + 1));
    }

    private TurboUser makeDummyUser() {
        return new TurboUser(dummyRepoId, "User " + (users.size() + 1));
    }

    protected List<TurboIssueEvent> getEvents(int issueId) {
        TurboIssue issueToGet = issues.get(issueId);
        if (issueToGet != null) {
            return issueToGet.getMetadata().getEvents();
        }
        // Fail silently
        return new ArrayList<>();
    }

    protected List<Comment> getComments(int issueId) {
        TurboIssue issueToGet = issues.get(issueId);
        if (issueToGet != null) {
            return issueToGet.getMetadata().getComments();
        }
        // Fail silently
        return new ArrayList<>();
    }

    // UpdateEvent methods to directly mutate the repo state
    protected void makeNewIssue() {
        TurboIssue toAdd = makeDummyIssue();
        issues.put(toAdd.getId(), toAdd);
        updatedIssues.put(toAdd.getId(), toAdd);
    }

    protected void makeNewLabel() {
        TurboLabel toAdd = makeDummyLabel();
        labels.put(toAdd.getActualName(), toAdd);
        updatedLabels.put(toAdd.getActualName(), toAdd);
    }

    protected void makeNewMilestone() {
        TurboMilestone toAdd = makeDummyMilestone();
        milestones.put(toAdd.getId(), toAdd);
        updatedMilestones.put(toAdd.getId(), toAdd);
    }

    protected void makeNewUser() {
        TurboUser toAdd = makeDummyUser();
        users.put(toAdd.getLoginName(), toAdd);
        updatedUsers.put(toAdd.getLoginName(), toAdd);
    }

    // Only updating of issues and milestones is possible. Labels and users are immutable.
    protected TurboIssue updateIssue(int itemId, String updateText) {
        TurboIssue issueToUpdate = issues.get(itemId);

        if (issueToUpdate != null) {
            return renameIssue(issueToUpdate, updateText);
        }
        return null;
    }

    private TurboIssue renameIssue(TurboIssue issueToUpdate, String updateText) {
        issueToUpdate.setTitle(updateText);

        // Add renamed event to events list of issue
        List<TurboIssueEvent> eventsOfIssue = issueToUpdate.getMetadata().getEvents();
        eventsOfIssue.add(new TurboIssueEvent(new User().setLogin("dummyUser"),
                IssueEventType.Renamed,
                new Date()));
        List<Comment> commentsOfIssue = issueToUpdate.getMetadata().getComments();
        issueToUpdate.setMetadata(new IssueMetadata(eventsOfIssue, commentsOfIssue));
        issueToUpdate.setUpdatedAt(LocalDateTime.now());

        // Add to list of updated issues
        updatedIssues.put(issueToUpdate.getId(), issueToUpdate);

        return issueToUpdate;
    }

    protected TurboMilestone updateMilestone(int itemId, String updateText) {
        TurboMilestone milestoneToUpdate = milestones.get(itemId);

        if (milestoneToUpdate != null) {
            return renameMilestone(milestoneToUpdate, updateText);
        }
        return null;
    }

    private TurboMilestone renameMilestone(TurboMilestone milestoneToUpdate, String updateText) {
        milestoneToUpdate.setTitle(updateText);
        updatedMilestones.put(milestoneToUpdate.getId(), milestoneToUpdate);

        return milestoneToUpdate;
    }

    protected TurboIssue deleteIssue(int itemId) {
        updatedIssues.remove(itemId);
        return issues.remove(itemId);
    }

    protected TurboLabel deleteLabel(String idString) {
        updatedLabels.remove(idString);
        return labels.remove(idString);
    }

    protected TurboMilestone deleteMilestone(int itemId) {
        updatedMilestones.remove(itemId);
        return milestones.remove(itemId);
    }

    protected TurboUser deleteUser(String idString) {
        updatedUsers.remove(idString);
        return users.remove(idString);
    }
}
