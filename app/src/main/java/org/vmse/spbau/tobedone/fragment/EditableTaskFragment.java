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
import org.vmse.spbau.tobedone.connection.model.TaskEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Egor Gorbunov on 11/4/15.
 * email: egor-mailbox@ya.ru
 */
public class EditableTaskFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    private TaskEntity taskEntity;
    private boolean isForUpdate = true; // or for new task creation

    private EditText editName;
    private EditText editDescription;
    private EditText editDeadline;
    private EditText editTags;
    private EditText editPriority;
    private Menu menu;
    private boolean[] chosenExistingTags;
    private String[] existingTags;
    private List<String> newTags;
    private long lastTimeEditTagPressed = -1;
    private boolean isEditable;
    EditText tv;

    public EditableTaskFragment() {
    }

    private void setIsEditable(boolean isEditable) {
        this.isEditable = isEditable;
        editName.setFocusableInTouchMode(isEditable);
        editName.setFocusable(isEditable);
        editName.setClickable(isEditable);
        editDescription.setFocusableInTouchMode(isEditable);
        editDescription.setFocusable(isEditable);
        editDescription.setClickable(isEditable);
        editDeadline.setFocusableInTouchMode(isEditable);
        editDeadline.setFocusable(isEditable);
        editDeadline.setClickable(isEditable);
        editTags.setFocusableInTouchMode(true);
        editTags.setFocusable(false);
        editTags.setClickable(isEditable);
        editPriority.setFocusableInTouchMode(isEditable);
        editPriority.setFocusable(isEditable);
        editPriority.setClickable(isEditable);
    }

    private void fillForm() {
        editName.setText(taskEntity.getTaskname());
        editDeadline.setText(taskEntity.getDeadline());
        editDescription.setText(taskEntity.getDescription());
        editPriority.setText(Long.toString(taskEntity.getPriority()));

        String tagString = "";
        if (taskEntity.getTags() != null) {
            for (String tag : taskEntity.getTags()) {
                tagString += tag + " ";
            }
        }
        editTags.setText(tagString);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        newTags = new LinkedList<>();
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
        tv = new EditText(getActivity());

        editTags.setOnClickListener(this);
        editTags.setOnLongClickListener(this);
        // before it entity and tags must be set
        if (!isForUpdate)
            fillDefault();

        fillForm();

        return view;
    }

    private void fillDefault() {
        taskEntity.setPriority(1);
        taskEntity.setTaskname("task" + new Random().nextInt(10000));
        taskEntity.setUsername(MainApplication.getTaskDataWrapper().getUsername());
        taskEntity.setDeadline("2015-09-05");
        taskEntity.setDescription("");
        taskEntity.setIsSolved(false);
        taskEntity.setTags(null);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        if (isForUpdate) {
            setIsEditable(false);
            getActivity().getMenuInflater().inflate(R.menu.task_fragment_menu, menu);
        } else {
            setIsEditable(true);
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

    private List<String> constructNewTags() {
        String newTags[] = editTags.getText().toString().replace("#", "").split("[,\\s;]");
        return Arrays.asList(newTags);
    }

    private TaskEntity constructNewTaskEntity() {
        TaskEntity newTaskEntity = taskEntity.copy();

        newTaskEntity.setTaskname(editName.getText().toString());
        newTaskEntity.setDescription(editDescription.getText().toString());
        newTaskEntity.setDeadline(editDeadline.getText().toString());
        newTaskEntity.setPriority(Long.valueOf(editPriority.getText().toString()));
        newTaskEntity.setTags(constructNewTags());

        return newTaskEntity;
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
                setIsEditable(true);
                return true;

            case R.id.action_remove:
                return true;

            case R.id.action_done:
                TaskEntity newTaskEntity = constructNewTaskEntity();
                newTaskEntity.setIsSolved(true);
                MainApplication.getTaskDataWrapper().updateTask(newTaskEntity);
                taskEntity = newTaskEntity;

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
                setIsEditable(false);
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

                if (isForUpdate) {
                    MainApplication.getTaskDataWrapper().updateTask(newEntity);
                } else {
                    MainApplication.getTaskDataWrapper().addTask(newEntity);
                }
                taskEntity = newEntity;

                if (isForUpdate)
                    Toast.makeText(getContext(), "Task updated!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Task created!", Toast.LENGTH_SHORT).show();

                isForUpdate = true;
                setIsEditable(false);

                // change menu
                menu.clear();
                getActivity().getMenuInflater().inflate(R.menu.task_fragment_menu, menu);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if (!isEditable)
            return;
        switch (v.getId()) {
            case R.id.editTags:
                showChooseExistingTagDIalog();
                break;
            }
        }

    void showChooseExistingTagDIalog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose existing tag");
        Collection<String> l = MainApplication.getTaskDataWrapper().getAllTags();

        chosenExistingTags = new boolean[l.size()];
        existingTags = new String[l.size()];
        int idx = 0;
        for (String tag : l) {
            chosenExistingTags[idx] = false;
            existingTags[idx] = tag;
            idx++;
        }
        builder.setMultiChoiceItems(existingTags, chosenExistingTags, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                chosenExistingTags[which] = isChecked;
            }


        });
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateTagsTextView();
            }
        });
        builder.show();
    }

    void showCreateNewTagDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Pick a name for new tag");
        builder.setView(tv);
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTag = tv.getText().toString();
                boolean exists = false;
                for (String s : existingTags) {
                    if (s == newTag)
                        exists = true;
                }
                for (String s : newTags) {
                    if (s == newTag)
                        exists = true;
                }
                if (!exists) {
                    newTags.add(newTag);
                    updateTagsTextView();
                }
            }
        });
        builder.show();
         }

    void updateTagsTextView() {
        String text = "";
        for (int i = 0; i < chosenExistingTags.length; i++) {
            if (chosenExistingTags[i]) {
                if (text != "")
                    text += ", " + existingTags[i];
                else
                    text += existingTags[i];
            }
        }
        for (String s : newTags) {
            if (text != "")
                text += ", " + s;
            else
                text += s;
        }
        editTags.setText(text);
    }

    @Override
    public boolean onLongClick(View v) {
        if (!isEditable)
            return false;
        switch (v.getId()) {
            case R.id.editTags:
                showCreateNewTagDialog();
                break;
        }
        return false;
    }
}
