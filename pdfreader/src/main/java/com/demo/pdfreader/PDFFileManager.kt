package com.demo.pdfreader

import android.util.SparseArray
import com.demo.pdfreader.entity.PDFFile

class PDFFileManager {

    companion object {
        private val fileList = ArrayList<PDFFile>()
        fun add(f: PDFFile) {
            fileList.add(f)
        }
        fun remove(f:PDFFile) {
            fileList.remove(f)
        }

        fun saveData(){}
        fun getData(): ArrayList<PDFFile> {
            return fileList
        }
    }
}