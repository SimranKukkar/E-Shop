package com.ecommerce.project.service;

import com.ecommerce.project.Repository.AddressRepository;
import com.ecommerce.project.Repository.UserRepository;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO,Address.class);
        address.setUser(user);
        user.getAddresses().add(address);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream().map(address ->modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(()-> new ResourceNotFoundException("addressId",addressId,"address"));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> addressList = user.getAddresses();
        return addressList.stream().map(address ->modelMapper.map(address, AddressDTO.class)).toList();


    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address address = addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("addressId",addressId,"address"));
        address.setStreet(addressDTO.getStreet());
        address.setBuildingName(addressDTO.getBuildingName());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setPincode(addressDTO.getPincode());
        addressRepository.save(address);
        User user = address.getUser();
        user.getAddresses().removeIf(add-> add.getAddressId().equals(addressId));
        user.getAddresses().add(address);
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("addressId",addressId,"address"));
        User user = address.getUser();
        user.getAddresses().removeIf(add-> add.getAddressId().equals(addressId));
        addressRepository.delete(address);
        return "Address deleted successfully with addressId: "+ addressId;
    }
}
