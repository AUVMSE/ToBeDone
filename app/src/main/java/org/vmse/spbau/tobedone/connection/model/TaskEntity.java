package org.vmse.spbau.tobedone.connection.model;

/**
 * @author antonpp
 * @since 04/11/15
 */
public class TaskEntity {

    public static final long CREATED_OFFLINE = -1;

    private Long id = CREATED_OFFLINE;
    private Long idUser;
    private String name;
    private String description;
    private Long priority;
    private String deadline;
    private Long breakTime;
    private Boolean isSolved;
    private Long elapsedTime; //seconds
    private String lastStop;

    public static long getCreatedOffline() {
        return CREATED_OFFLINE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public Long getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(Long breakTime) {
        this.breakTime = breakTime;
    }

    public void setBreakTime(long breakTime) {
        this.breakTime = breakTime;
    }

    public Boolean isSolved() {
        return isSolved;
    }

    public void setIsSolved(Boolean isSolved) {
        this.isSolved = isSolved;
    }

    public void setIsSolved(boolean isSolved) {
        this.isSolved = isSolved;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getLastStop() {
        return lastStop;
    }

    public void setLastStop(String lastStop) {
        this.lastStop = lastStop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskEntity that = (TaskEntity) o;

         if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
      if (getIdUser() != null ? !getIdUser().equals(that.getIdUser()) : that.getIdUser() != null)
           return false;
   if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
          return false;
    if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
          return false;
       if (getPriority() != null ? !getPriority().equals(that.getPriority()) : that.getPriority() != null)
           return false;
      if (getDeadline() != null ? !getDeadline().equals(that.getDeadline()) : that.getDeadline() != null)
           return false;
      if (getBreakTime() != null ? !getBreakTime().equals(that.getBreakTime()) : that.getBreakTime() != null)
          return false;
     if (isSolved != null ? !isSolved.equals(that.isSolved) : that.isSolved != null)
           return false;
      if (getElapsedTime() != null ? !getElapsedTime().equals(that.getElapsedTime()) : that.getElapsedTime() != null)
           return false;
    return !(getLastStop() != null ? !getLastStop().equals(that.getLastStop()) : that.getLastStop() != null);
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getIdUser() != null ? getIdUser().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getPriority() != null ? getPriority().hashCode() : 0);
        result = 31 * result + (getDeadline() != null ? getDeadline().hashCode() : 0);
        result = 31 * result + (getBreakTime() != null ? getBreakTime().hashCode() : 0);
        result = 31 * result + (isSolved != null ? isSolved.hashCode() : 0);
        result = 31 * result + (getElapsedTime() != null ? getElapsedTime().hashCode() : 0);
        result = 31 * result + (getLastStop() != null ? getLastStop().hashCode() : 0);
        return result;
    }

    public TaskEntity copy() {
        TaskEntity newTaskEntity = new TaskEntity();

        newTaskEntity.setId(getId());
        newTaskEntity.setBreakTime(getBreakTime());
        newTaskEntity.setDeadline(getDeadline());
        newTaskEntity.setDescription(getDescription());
        newTaskEntity.setElapsedTime(getElapsedTime());
        newTaskEntity.setIsSolved(isSolved());
        newTaskEntity.setLastStop(getLastStop());
        newTaskEntity.setPriority(getPriority());
        newTaskEntity.setIdUser(getIdUser());
        newTaskEntity.setName(getName());

        return newTaskEntity;
    }
}
