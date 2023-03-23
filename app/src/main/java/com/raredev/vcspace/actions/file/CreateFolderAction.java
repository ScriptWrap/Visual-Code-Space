package com.raredev.vcspace.actions.file;

import android.view.LayoutInflater;
import android.widget.EditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.LayoutTextinputBinding;
import com.raredev.vcspace.fragments.TreeViewFragment;
import com.unnamed.b.atv.model.TreeNode;
import java.io.File;

public class CreateFolderAction extends FileAction {

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
    binding.tvInputLayout.setHint(fragment.getString(R.string.folder_name_hint));

    new MaterialAlertDialogBuilder(fragment.requireActivity())
        .setTitle(R.string.new_folder_title)
        .setPositiveButton(
            R.string.create,
            (dlg, i) -> {
              File newFolder = new File(node.getValue(), "/" + et_filename.getText().toString());
              if (!newFolder.exists()) {
                if (newFolder.mkdirs()) {
                  fragment.addNewChild(node, newFolder);
                  fragment.expandNode(node);
                }
              }
            })
        .setNegativeButton(R.string.cancel, null)
        .setView(binding.getRoot())
        .show();
  }

  @Override
  public int getIcon() {
    return R.drawable.folder_plus_outline;
  }

  @Override
  public int getTitle() {
    return R.string.new_folder_title;
  }
}