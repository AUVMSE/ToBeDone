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
import android.widget.Toast;

import org.vmse.spbau.tobedone.MainApplication;
import org.vmse.spbau.tobedone.R;
import org.vmse.spbau.tobedone.activity.ToBeDoneActivity;
import org.vmse.spbau.tobedone.connection.TaskDataWrapper;
import org.vmse.spbau.tobedone.connection.Util;
import org.vmse.spbau.tobedone.connection.model.TaskEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Egor Gorbunov on 11/4/15.
 * email: egor-mailbox@ya.ru
 */
public class EditableTaskFragment extends Fragment {
    private TaskEntity taskEntity;
    private List<String> tags;
    private boolean isForUpdate = true; // or for new task creation

    private EditText editName;
    private EditText editDescription;
    private EditText editDeadline;
    private EditText editTags;
    private EditText editPriority;
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
        editPriority.setFocusableInTouchMode(false);
        editPriority.setFocusable(false);
        editPriority.setClickable(false);

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
        editPriority.setFocusableInTouchMode(true);
        editPriority.setFocusable(true);
        editPriority.setClickable(true);
    }

    private void fillForm() {
        editName.setText(taskEntity.getName());
        editDeadline.setText(taskEntity.getDeadline());
        editDescription.setText(taskEntity.getDescription());
        editPriority.setText(Long.toString(taskEntity.getPriority()));

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
        editPriority = (EditText) view.findViewById(R.id.editPriority);

        // before it entity and tags must be set
        if (!isForUpdate)
            fillDefault();

        fillForm();

        return view;
    }

    private void fillDefault() {
        taskEntity.setPriority(0);
        taskEntity.setIdUser(MainApplication.getTaskDataWrapper().getUserId());
        taskEntity.setName("");
        taskEntity.setDeadline("2015-09-05");
        taskEntity.setDescription("");
        taskEntity.setIsSolved(false);

//        tags = new ArrayList<String>() {{
//            add("university");
//            add("study");
//            add("ha");
//        }};
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        if (isForUpdate) {
            setAllUneditable();
            getActivity().getMenuInflater().inflate(R.menu.task_fragment_menu, menu);
        } else {
            setAllEditable();
            getActivity().getMenuInflater().inflate(R.menu.task_edit_menu, menu);
        }
    }

    public void setTaskEntity(TaskEntity taskEntity, boolean isForUpdate) {
        this.isForUpdate = isForUpdate;

        if (taskEntity == null) {
            throw new NullPointerException("Task Entity can't be null!");
        }

        this.taskEntity = taskEntity;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    private String checkFields() {
        if (editName.length() <= 0) {
            return "Task name can't be empty!";
        } else if (editDeadline.length() <= 0) {
            return "Dead line can't be empty!";
        } else if (editPriority.length() <= 0) {
            return "Priority can't be empty!";
        }

        return null;
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
        newTaskEntity.setPriority(Long.valueOf(editPriority.getText().toString()));

        return newTaskEntity;
    }

    private List<String> constructNewTags() {
        String newTags[] = editTags.getText().toString().replace("#", "").split("[,\\s;]");
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
                // change menu
                menu.clear();
                getActivity().getMenuInflater().inflate(R.menu.task_edit_menu, menu);
                setAllEditable();
                return true;

            case R.id.action_remove:
                ((ToBeDoneActivity) getActivity()).removeTask(taskEntity);
                return true;

            case R.id.action_done:
                TaskEntity newTaskEntity = constructNewTaskEntity();
                newTaskEntity.setIsSolved(true);
                try {
                    MainApplication.getTaskDataWrapper().updateTask(newTaskEntity, taskEntity);
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
                setAllUneditable();
                // change menu
                menu.clear();
                getActivity().getMenuInflater().inflate(R.menu.task_fragment_menu, menu);

                getActivity().onBackPressed();

                return true;

            case R.id.action_save:
                String resCheck = checkFields();
                if (resCheck != null) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Bad input!")
                            .setMessage(resCheck)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return true;
                }

                TaskEntity newEntity = constructNewTaskEntity();
                List<String> newTags = constructNewTags();

                try {
                    if (isForUpdate) {
                        MainApplication.getTaskDataWrapper().updateTask(newEntity, taskEntity);
                        MainApplication.getTaskDataWrapper().updateTaskTags(taskEntity, newTags);
                    } else {
                        MainApplication.getTaskDataWrapper().addTask(newEntity, newTags);
                    }
                    taskEntity = newEntity;
                    tags = newTags;
                } catch (TaskDataWrapper.SyncException e) {
                    e.printStackTrace();
                }

                if (isForUpdate)
                    Toast.makeText(getContext(), "Task updated!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Task created!", Toast.LENGTH_SHORT).show();

                isForUpdate = true;
                setAllUneditable();
                // change menu
                menu.clear();
                getActivity().getMenuInflater().inflate(R.menu.task_fragment_menu, menu);


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
