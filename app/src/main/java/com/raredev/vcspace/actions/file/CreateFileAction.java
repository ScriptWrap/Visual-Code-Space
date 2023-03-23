package com.raredev.vcspace.actions.file;

import android.view.LayoutInflater;
import android.widget.EditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.LayoutTextinputBinding;
import com.raredev.vcspace.fragments.TreeViewFragment;
import com.unnamed.b.atv.model.TreeNode;
import java.io.File;
import java.io.IOException;

public class CreateFileAction extends FileAction {

  @Override
  public boolean isApplicable(File file) {
    return file.isDirectory();
  }

  @Override
  public void performAction() {
    TreeViewFragment fragment = (TreeViewFragment) getActionEvent().getData("fragment");
    TreeNode node = (TreeNode) getActionEvent().getData("node");

    LayoutTextinputBinding binding =
        LayoutTextinputBinding.inflate(LayoutInflater.from(fragment.requireActivity()));
    EditText et_filename = binding.etInput;
    binding.tvInputLayout.setHint(fragment.getString(R.string.file_name_hint));

    new MaterialAlertDialogBuilder(fragment.requireActivity())
        .setTitle(R.string.new_file_title)
        .setPositiveButton(
            R.string.create,
            (dlg, i) -> {
              try {
                File newFile = new File(node.getValue(), "/" + et_filename.getText().toString());
                if (!newFile.exists()) {
                  if (newFile.createNewFile()) {
                    fragment.addNewChild(node, newFile);
                    fragment.expandNode(node);
                  }
                }
              } catch (IOException ioe) {
              }
            })
        .setNegativeButton(R.string.cancel, null)
        .setView(binding.getRoot())
        .show();
  }

  @Override
  public int getIcon() {
    return R.drawable.file_plus_outline;
  }

  @Override
  public int getTitle() {
    return R.string.new_file_title;
  }
}