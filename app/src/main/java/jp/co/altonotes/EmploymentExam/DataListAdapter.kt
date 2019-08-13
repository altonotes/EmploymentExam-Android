package jp.co.altonotes.EmploymentExam

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray


class DataListAdapter(context: Context) : RecyclerView.Adapter<DataListAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    var data: JsonArray? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.data_list_cell, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        // データ表示
        val item = data?.get(i)?.asJsonObject
        if (item != null) {
            val code = item.get("code")?.asString
            val name = item.get("name")?.asString
            viewHolder.valueLabel.text = "[$code] $name"
        }
    }

    override fun getItemCount(): Int {
        return data?.size() ?: 0
    }

    // ViewHolder(固有ならインナークラスでOK)
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var valueLabel: AppCompatTextView = itemView.findViewById(R.id.valueLabel) as AppCompatTextView

    }

}