package com.example.writingnotes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_notes_list.*
import kotlinx.android.synthetic.main.note_item.view.*
import java.util.ArrayList


class NotesListFragment : Fragment() {

    val listNote = ArrayList<Note>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        // Get All Data From SqlData Base
        QuerySearch("%")
        // *********************************


    }

    fun QuerySearch(title: String) {

        var dbManager = DB_Manager(this!!.requireActivity())
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections, "Title like ?", selectionArgs, "Title")

        if (cursor.moveToFirst()) {
            listNote.clear()
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))

                listNote.add(Note(ID, Title, Description))

            } while (cursor.moveToNext())

        }

        var myAdapter = NoteAdapter(this!!.requireActivity(), listNote)
        list_view_show_note.adapter = myAdapter

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater?.inflate(R.menu.noteslist_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item!!.itemId) {
            R.id.add_notes_btn -> {
                requireView().findNavController()
                    .navigate(R.id.action_notesListFragment_to_addNoteFragment)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // Adapter ******************************************************
    inner class  NoteAdapter: BaseAdapter {
        var listaAdapter = ArrayList<Note>()
        var context:Context?=null

        constructor(context:Context , listaAdapter:ArrayList<Note>) : super()
        {
            this.context=context
            this.listaAdapter=listaAdapter
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
        {

            val note = this.listaAdapter[position]
            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var noteView= inflator.inflate(R.layout.note_item,null)
            noteView.tvTitle.text = note.nodeName
            noteView.tvDes.text = note.nodeDes

            noteView.ivDelete.setOnClickListener{

                var dbManager=DB_Manager(this.context!!)
                val selectionArgs= arrayOf(note.nodeID.toString())
                dbManager.Delete("ID=?",selectionArgs)

                println(Toast.makeText(this!!.context, " Note Deleted ", Toast.LENGTH_LONG).show())

                QuerySearch("%")
            }

            noteView.ivEdit.setOnClickListener{

                GoToUpdate(note)
            }

            return noteView
        }

        override fun getItem(position: Int): Any
        {
            return listaAdapter[position]

        }

        override fun getItemId(position: Int): Long
        {
            return position.toLong()
        }

        override fun getCount(): Int
        {
            return listaAdapter.size
        }
    }

    // ****************************************************************

    private fun GoToUpdate(note: Note) {

        var bundle= Bundle()
        bundle.putInt("ID",note.nodeID!!)
        bundle.putString("Title",note.nodeName!!)
        bundle.putString("Description",note.nodeDes!!)

        requireView().findNavController().navigate(R.id.action_notesListFragment_to_addNoteFragment , bundle)
    }

}