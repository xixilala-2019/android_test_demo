package com.demo.pdfreader

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.demo.pdfreader.databinding.ItemPdfFileBinding

class PDFFileAdapter:RecyclerView.Adapter<PDFFileAdapter.Holder>()  {

    private val layoutInflater:LayoutInflater
    constructor(context: Context) {
        layoutInflater = LayoutInflater.from(context)
    }

    class Holder(pdfBinding:ItemPdfFileBinding) : RecyclerView.ViewHolder(pdfBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val pdfBinding = DataBindingUtil.inflate<ItemPdfFileBinding>(layoutInflater,R.layout.item_pdf_file,parent,false)
        return Holder(pdfBinding)
    }

    override fun getItemCount() = PDFFileManager.getData().size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        TODO("Not yet implemented")
    }
}