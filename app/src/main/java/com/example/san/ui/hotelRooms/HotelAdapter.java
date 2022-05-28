package com.example.san.ui.hotelRooms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.san.databinding.RoomItemBinding;
import com.example.san.entities.BookedHotel;
import com.example.san.entities.Hotel;
import com.example.san.ui.bookedRoom.BookedHotelViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.RoomHolder>{
    private List<Hotel> hotels = new ArrayList<>();
    private HotelViewModel hotelViewModel;
    private BookedHotelViewModel bookedHotelViewModel;
    private HotelsFragment hotelsFragment;
    private FragmentManager fragmentManager;

    @NonNull
    @Override
    public HotelAdapter.RoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RoomItemBinding binding = RoomItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HotelAdapter.RoomHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelAdapter.RoomHolder holder, int position) {
        Hotel hotel = hotels.get(position);

        holder.binding.hotelName.setText(hotel.getName());
        holder.binding.hotelImage.setImageResource(hotel.getPhotoResource());

//        holder.binding.hotelBookButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int pos = holder.getAdapterPosition();
//                Hotel pickedHotel = hotels.get(pos);
//                String message = "Вы забронировали комнату: " + pickedHotel.getName();
//                Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
//                pickedHotel.setIsReserved(1);
//                hotelViewModel.update(pickedHotel);
//            }
//        });

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        Long today = MaterialDatePicker.todayInUtcMilliseconds();
        calendar.setTimeInMillis(today);

        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setValidator(DateValidatorPointForward.now());

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("SELECT A DATE");
        builder.setCalendarConstraints(constraintBuilder.build());
        final MaterialDatePicker materialDatePicker = builder.build();

        holder.binding.hotelBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(fragmentManager, "DATE_PICKER");
            }
        });
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                String date = materialDatePicker.getHeaderText();
                int pos = holder.getAdapterPosition();
                Hotel pickedHotel = hotels.get(pos);
                String message = "Вы забронировали комнату: " + pickedHotel.getName();
                Toast.makeText(hotelsFragment.requireContext(), message, Toast.LENGTH_SHORT).show();
                BookedHotel bookedHotel = new BookedHotel(pickedHotel.getName(), pickedHotel.getPhotoResource(), date);
                bookedHotelViewModel.insert(bookedHotel);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();

                if(pos != RecyclerView.NO_POSITION){
                    String message = hotels.get(pos).getName() + ", позиция в листе - " + pos;
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            AlertDialog alertDialog = new AlertDialog.Builder(holder.itemView.getContext()).setMessage("Вы хотите удалить")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            hotelViewModel.delete(hotel);
                            String message = "Комната " + hotel.getName() + " была удалена.";
                            Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("НЕТ", null).create();
            alertDialog.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    public void setFragment(FragmentManager parentFragmentManager) {
        this.fragmentManager = parentFragmentManager;
    }

    public static class RoomHolder extends RecyclerView.ViewHolder {
        RoomItemBinding binding;

        public RoomHolder(@NonNull RoomItemBinding roomItemBinding) {
            super(roomItemBinding.getRoot());
            this.binding = roomItemBinding;
        }
    }

    public void setRooms(List<Hotel> hotels){
        this.hotels = hotels;
        notifyDataSetChanged();
    }

    public void setRoomViewModel(HotelViewModel hotelViewModel){
        this.hotelViewModel = hotelViewModel;
        notifyDataSetChanged();
    }

    public void setRoomsFragment(HotelsFragment hotelsFragment){
        this.hotelsFragment = hotelsFragment;
        notifyDataSetChanged();
    }

    public void setBookedHotelViewModel(BookedHotelViewModel bookedHotelViewModel) {
        this.bookedHotelViewModel = bookedHotelViewModel;
        notifyDataSetChanged();
    }
}