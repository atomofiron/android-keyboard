package demo.atomofiron.keyboard

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import demo.atomofiron.keyboard.databinding.ItemCategoryBinding

class CategoryHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

private class DiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
}

class CategoriesAdapter : ListAdapter<String, CategoryHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val icon = ContextCompat.getDrawable(holder.textView.context, R.drawable.category)
        holder.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, icon, null, null)
        holder.textView.text = getItem(position)
    }
}