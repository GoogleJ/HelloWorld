//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.RongBaseNoActionbarActivity;
import io.rong.imkit.fragment.FileListFragment;

public class FileListActivity extends RongBaseNoActionbarActivity {
  private int fragmentCount = 0;

  public FileListActivity() {
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.getWindow().setFlags(2048, 2048);
    this.requestWindowFeature(1);
    this.setContentView(layout.rc_ac_file_list);
    if (this.getSupportFragmentManager().findFragmentById(id.rc_ac_fl_storage_folder_list_fragment) == null) {
      FileListFragment fileListFragment = new FileListFragment();
      this.showFragment(fileListFragment);
    }
  }

  public void showFragment(Fragment fragment) {
    ++this.fragmentCount;
    this.getSupportFragmentManager().beginTransaction().addToBackStack(this.fragmentCount + "").replace(id.rc_ac_fl_storage_folder_list_fragment, fragment).commitAllowingStateLoss();
  }

  public void onBackPressed() {
    if (--this.fragmentCount == 0) {
      FragmentManager fm = this.getSupportFragmentManager();

      for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
        FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(i);
        Fragment fragment = fm.findFragmentByTag(entry.getName());
        if (fragment != null) {
          fragment.onDestroy();
        }
      }

      this.finish();
    } else {
      super.onBackPressed();
    }

  }
}
