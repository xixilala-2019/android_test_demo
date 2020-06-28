package com.demo.pdfreader.entity


class PDFFile {
    var title = ""
    var inputTime = 0L
    var read_page_num = 0
    var total_page_num = 0
    var abs_file_path = ""

    fun getProgress(): Int {
        return read_page_num/total_page_num*100
    }
}