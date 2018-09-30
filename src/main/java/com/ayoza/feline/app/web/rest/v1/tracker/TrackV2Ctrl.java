package com.ayoza.feline.app.web.rest.v1.tracker;

import static com.ayoza.feline.app.web.rest.v1.tracker.TrackerUtils.convertToDecimalDegrees;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.ayoza.feline.app.web.rest.v1.access.AccessControl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ayoza.com.feline.api.entities.tracker.dto.PointDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/v2/tracks")
@RequiredArgsConstructor
public class TrackV2Ctrl {

    private final AccessControl accessControl;
    
    private final TrackService trackService;

    @RequestMapping(value = "", method = POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_FORM_URLENCODED_VALUE, headers="Accept=*/*")
    @ResponseBody
    public void addPointV1(
                    @RequestParam(value="latitude", required=true)  String ggaLatitude, // 4025.7313N
                    @RequestParam(value="longitude", required=true)  String ggaLongitude, // 00338.5613W
                    @RequestParam(value="accuracy", required=false)  Double accuracy,
                    @RequestParam(value="altitude", required=false)  Double altitude) {
        
        PointDTO pointDTO = PointDTO.builder()
                .accuracy(accuracy)
                .altitude(altitude)
                .latitude(convertToDecimalDegrees(ggaLatitude))
                .longitude(convertToDecimalDegrees(ggaLongitude))
                .build();
        trackService.addPoint(pointDTO, accessControl.getUserIdFromSecurityContext());
    }
}
