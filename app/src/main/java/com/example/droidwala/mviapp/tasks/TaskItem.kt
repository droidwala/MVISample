package com.example.droidwala.mviapp.tasks

import com.example.droidwala.mviapp.R
import com.example.droidwala.mviapp.data.Task
import com.example.droidwala.mviapp.databinding.ItemTaskBinding
import com.xwray.groupie.Item

/**
 * Created by punitdama on 07/01/18.
 */
class TaskItem(val task: Task) : Item<ItemTaskBinding>(){

    override fun getLayout() = R.layout.item_task

    override fun bind(viewBinding: ItemTaskBinding, position: Int) {
        viewBinding.title.text = task.title
        viewBinding.desc.text = task.description ?: ""
    }


}