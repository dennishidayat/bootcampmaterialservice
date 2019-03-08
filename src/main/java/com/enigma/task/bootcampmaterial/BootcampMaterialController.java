package com.enigma.task.bootcampmaterial;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.enigma.task.bootcamp.dao.BootcampPeriodDao;
import com.enigma.task.bootcamp.model.BootcampPeriod;
import com.enigma.task.bootcampmaterial.dao.BootcampMaterialDao;
import com.enigma.task.bootcampmaterial.dto.BootcampMaterialDto;
import com.enigma.task.bootcampmaterial.dto.CommonResponse;
import com.enigma.task.bootcampmaterial.exception.CustomException;
import com.enigma.task.bootcampmaterial.model.BootcampMaterial;

@RestController
@RequestMapping("/bootcampmaterial")
@SuppressWarnings("rawtypes")
public class BootcampMaterialController {
	
	@Autowired
	public ModelMapper modelMapper;

	@Autowired
	public BootcampMaterialDao bootcampMaterialDao;
	
	@Autowired
	public BootcampPeriodDao bootcampPeriodDao;

	@GetMapping(value="/{bootcampmaterialid}")
	public CommonResponse<BootcampMaterialDto> getById(@PathVariable("bootcampmaterialid") String bcMaterialId) throws CustomException {
		try {
			BootcampMaterial bootcampMaterial = bootcampMaterialDao.getById(Integer.parseInt(bcMaterialId));
			
			return new CommonResponse<BootcampMaterialDto>(modelMapper.map(bootcampMaterial, BootcampMaterialDto.class));
		} catch (CustomException e) {
			return new CommonResponse<BootcampMaterialDto>("06", e.getMessage());
		} catch (Exception e) {
			return new CommonResponse<BootcampMaterialDto>("14", e.getMessage());
		}
	}
	
	@PostMapping(value="")
	public CommonResponse<BootcampMaterialDto> insert(@RequestBody BootcampMaterialDto bootcampMaterialDto) throws CustomException {
		try {
			BootcampMaterial bootcampMaterial = modelMapper.map(bootcampMaterialDto, BootcampMaterial.class);
			bootcampMaterial.setId(0);
			bootcampMaterial = bootcampMaterialDao.save(bootcampMaterial);
			
			return new CommonResponse<BootcampMaterialDto>(modelMapper.map(bootcampMaterial, BootcampMaterialDto.class));
		} catch (CustomException e) {
			return new CommonResponse<BootcampMaterialDto>("14", "study period not found");
		} catch (NumberFormatException e) {
			return new CommonResponse<BootcampMaterialDto>();
		} catch (Exception e) {
			return new CommonResponse<BootcampMaterialDto>();
		}
	}
	
	@PutMapping(value="")
	public CommonResponse<BootcampMaterialDto> update(@RequestBody BootcampMaterialDto bootcampMaterialDto) throws CustomException {
		try {
			BootcampMaterial bootcampMaterial = bootcampMaterialDao.getById(bootcampMaterialDto.getId());
			if (bootcampMaterial == null) {
				return new CommonResponse<BootcampMaterialDto>("14", "bootcamp material id not found");
			}
			if (bootcampMaterialDto.getActiveFlag() != null) {
				bootcampMaterial.setActiveFlag(bootcampMaterialDto.getActiveFlag());
			}
			if (bootcampMaterialDto.getBootcampPeriod() != null) {
				bootcampMaterial.setBootcampPeriod(bootcampMaterialDto.getBootcampPeriod());
			}
			if (bootcampMaterialDto.getDescription() != null) {
				bootcampMaterial.setDescription(bootcampMaterialDto.getDescription());
			}
			if (bootcampMaterialDto.getStudyMaterial() != null) {
				bootcampMaterial.setStudyMaterial(bootcampMaterialDto.getStudyMaterial());
			}
			if (bootcampMaterialDto.getTrainer() != null) {
				bootcampMaterial.setTrainer(bootcampMaterialDto.getTrainer());
			}
			
			bootcampMaterial = bootcampMaterialDao.save(bootcampMaterial);
			return new CommonResponse<BootcampMaterialDto>(modelMapper.map(bootcampMaterial, BootcampMaterialDto.class));
		} catch (CustomException e) {
			return new CommonResponse("01", e.getMessage());
		} catch (Exception e) {
			return new CommonResponse("06", e.getMessage());
		}
	}
	
	@DeleteMapping(value="/{bootcampmaterialid}")
	public CommonResponse<BootcampMaterialDto> delete(@PathVariable("bootcampmaterialid") String bootcampMaterialId) throws CustomException {
		
		try {
			BootcampMaterial bootcampMaterial = bootcampMaterialDao.getById(Integer.parseInt(bootcampMaterialId));
			if (bootcampMaterial == null) {
				return new CommonResponse("06", "trainee not found");
			}
			bootcampMaterialDao.delete(bootcampMaterial);
			return new CommonResponse<BootcampMaterialDto>();
		} catch (CustomException e) {
			return new CommonResponse("01", e.getMessage());
		} catch (Exception e) {
			return new CommonResponse("06", e.getMessage());
		}
	}
	
	@GetMapping(value="")
	public CommonResponse<List<BootcampMaterialDto>> getList(@RequestParam(name="list", defaultValue="") String id) throws CustomException {
		try {
			List<BootcampMaterial> bootcampMaterials = bootcampMaterialDao.getList();
			
			return new CommonResponse<List<BootcampMaterialDto>>(bootcampMaterials.stream().map(bcMaterials -> modelMapper.map(bcMaterials, BootcampMaterialDto.class)).collect(Collectors.toList()));
		} catch (CustomException e) {
			throw e;
		} catch(NumberFormatException e) {
			return new CommonResponse("01", e.getMessage());
		} catch (Exception e) {
			return new CommonResponse("06", e.getMessage());
		}
	}
	
	@GetMapping(value="/active")
	public CommonResponse<List<BootcampMaterialDto>> getListByActiveFlag(@RequestParam(name="list", defaultValue="") String id) throws CustomException {

		try {
			List<BootcampMaterial> bootcampMaterials;
			BootcampPeriod bootcampPeriod = bootcampPeriodDao.getById(Integer.parseInt(id));
			bootcampMaterials = bootcampMaterialDao.getBootcampPeriodList(bootcampPeriod);
			bootcampMaterials = bootcampMaterialDao.getListByActiveFlag();
			
			return new CommonResponse<List<BootcampMaterialDto>>(bootcampMaterials.stream().map(bcMaterials -> modelMapper.map(bcMaterials, BootcampMaterialDto.class)).collect(Collectors.toList()));
		} catch (CustomException e) {
			throw e;
		} catch(NumberFormatException e) {
			return new CommonResponse("01", e.getMessage());
		} catch (Exception e) {
			return new CommonResponse("06", e.getMessage());
		}
	}
	
}
