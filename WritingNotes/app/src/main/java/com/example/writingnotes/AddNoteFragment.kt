package com.example.writingnotes

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_add_note.*

class AddNoteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    var id:Int? = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adding_toLista.setOnClickListener{
            AddNewNote()

        }
        setHasOptionsMenu(true)

        // Check view updated
         id = arguments?.getInt("ID")
        if(id !=null)
        {
            val title = arguments?.getString("Title")
            edit_text_title.setText(title)
            val des = arguments?.getString("Description")
            edit_text_description.setText(des)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater?.inflate(R.menu.add_notes_menu , menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item!!.itemId)
        {
            R.id.backbtn ->{
                requireView().findNavController().navigate(R.id.notesListFragment)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun AddNewNote()
    {
        val title = edit_text_title.text.toString()
        val description = edit_text_description.text.toString()

        if(title.equals(""))
        {
            println(Toast.makeText(this!!.context, " Enter Title ", Toast.LENGTH_LONG).show())
        }
        else if (description.equals(""))
        {
            println(Toast.makeText(this!!.context, " Enter Description ", Toast.LENGTH_LONG).show())
        }
        else
        {
            val values = ContentValues()
            values.put("Title", title)
            values.put("Description", description)

            val dbManager = DB_Manager(this!!.requireActivity()!!)

            if(id == 0)
            {
                // Add
                val ID = dbManager.Insert(values)
                if (ID > 0)
                {
                    println(Toast.makeText(this!!.activity, " note is added", Toast.LENGTH_LONG).show())

                } else
                {
                    println(Toast.makeText(this!!.activity, " cannot add note ", Toast.LENGTH_LONG).show())
                }

            }

            else
            {
                // Edit
                var selectionArgs = arrayOf(id.toString())
                val ID = dbManager.Update(values,"ID=?",selectionArgs)
                if (ID > 0)
                {
                    println(Toast.makeText(this!!.activity, " note is Updated", Toast.LENGTH_LONG).show())
                } else
                {
                    println(Toast.makeText(this!!.activity, " Fail to Update note ", Toast.LENGTH_LONG).show())
                }


            }



        }

    }


}