package org.vmse.spbau.tobedone.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.vmse.spbau.tobedone.MainApplication;
import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.activity.ToBeDoneActivity;
import org.vmse.spbau.tobedone.connection.TaskDataWrapper;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Egor Gorbunov on 11/4/15.
 * email: egor-mailbox@ya.ru
 */
public class EditableTaskFragment extends Fragment {
    private TaskEntity taskEntity;
    private List<String> tags;

    private EditText editName;
    private EditText editDescription;
    private EditText editDeadline;
    private EditText editTags;
    private Menu menu;

    public EditableTaskFragment() {
    }


    private void setAllUneditable() {
        editName.setFocusableInTouchMode(false);
        editName.setFocusable(false);
        editName.setClickable(false);
        editName.setFocusableInTouchMode(false);
        editDescription.setFocusable(false);
        editDescription.setClickable(false);
        editName.setFocusableInTouchMode(false);
        editDeadline.setFocusable(false);
        editDeadline.setClickable(false);
        editName.setFocusableInTouchMode(false);
        editTags.setFocusable(false);
        editTags.setClickable(false);
    }

    private void setAllEditable() {
        editName.setFocusableInTouchMode(true);
        editName.setFocusable(true);
        editName.setClickable(true);
        editDescription.setFocusableInTouchMode(true);
        editDescription.setFocusable(true);
        editDescription.setClickable(true);
        editDeadline.setFocusableInTouchMode(true);
        editDeadline.setFocusable(true);
        editDeadline.setClickable(true);
        editTags.setFocusableInTouchMode(true);
        editTags.setFocusable(true);
        editTags.setClickable(true);
    }

    private void fillForm() {
        editName.setText(taskEntity.getName());
        editDeadline.setText(taskEntity.getDeadline());
        editDescription.setText(taskEntity.getDescription());

        String tagString = "";
        if (tags != null) {
            for (String tag : tags) {
                tagString += "#" + tag + " ";
            }
        }
        editTags.setText(tagString);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.editable_task, container, false);

        editName = (EditText) view.findViewById(R.id.editName);
        editDescription = (EditText) view.findViewById(R.id.editDescription);
        editDeadline = (EditText) view.findViewById(R.id.editDeadline);
        editTags = (EditText) view.findViewById(R.id.editTags);

        setAllUneditable();

        // before it entity and tags must be set
        fillForm();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        getActivity().getMenuInflater().inflate(R.menu.task_fragment_menu, menu);
    }

    public void setTaskEntity(TaskEntity taskEntity) {
        if (taskEntity == null) {
            throw new NullPointerException("Task Entity can't be null!");
        }

        this.taskEntity = taskEntity;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    private boolean checkFields() {
        return editName.length() > 0 && editDeadline.length() > 0;
    }

    private TaskEntity constructNewTaskEntity() {
        TaskEntity newTaskEntity = new TaskEntity();

        newTaskEntity.setId(taskEntity.getId());
        newTaskEntity.setBreakTime(taskEntity.getBreakTime());
        newTaskEntity.setDeadline(taskEntity.getDeadline());
        newTaskEntity.setDescription(taskEntity.getDescription());
        newTaskEntity.setElapsedTime(taskEntity.getElapsedTime());
        newTaskEntity.setIsSolved(taskEntity.isSolved());
        newTaskEntity.setLastStop(taskEntity.getLastStop());
        newTaskEntity.setPriority(taskEntity.getPriority());
        newTaskEntity.setIdUser(taskEntity.getIdUser());
        newTaskEntity.setName(taskEntity.getName());


        newTaskEntity.setName(editName.getText().toString());
        newTaskEntity.setDescription(editDescription.getText().toString());
        newTaskEntity.setDeadline(editDeadline.getText().toString());

        return newTaskEntity;
    }

    private List<String> constructNewTags() {
        String newTags[] = editTags.getText().toString().split("[,\\s;]");
        return Arrays.asList(newTags);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (taskEntity == null) {
            throw new NullPointerException("Task Entity can't be null!");
        }

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_edit:
                //TODO: handle edit
                setAllEditable();
                menu.clear();
                getActivity().getMenuInflater().inflate(R.menu.task_edit_menu, menu);
                return true;

            case R.id.action_remove:
                ((ToBeDoneActivity) getActivity()).removeTask(taskEntity);
                return true;

            case R.id.action_done:
                TaskEntity newTaskEntity = constructNewTaskEntity();
                newTaskEntity.setIsSolved(true);
                try {
                    MainApplication.getTaskDataWrapper().updateTask(taskEntity, newTaskEntity);
                    taskEntity = newTaskEntity;
                } catch (TaskDataWrapper.SyncException e) {
                    e.printStackTrace();
                }

                new AlertDialog.Builder(getContext())
                        .setTitle("Task solved!")
                        .setMessage("Congratulations with one more solved task!")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().onBackPressed();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();

                return true;

            case R.id.action_discard:
                menu.clear();
                getActivity().getMenuInflater().inflate(R.menu.task_fragment_menu, menu);
                setAllUneditable();
                return true;

            case R.id.action_save:
                checkFields();
                TaskEntity newEntity = constructNewTaskEntity();
                List<String> newTags = constructNewTags();

                try {
                    MainApplication.getTaskDataWrapper().updateTask(taskEntity, newEntity);
                    MainApplication.getTaskDataWrapper().updateTaskTags(taskEntity, newTags);
                    taskEntity = newEntity;
                    tags = newTags;
                } catch (TaskDataWrapper.SyncException e) {
                    e.printStackTrace();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
