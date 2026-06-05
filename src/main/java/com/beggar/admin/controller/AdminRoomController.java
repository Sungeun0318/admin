package com.beggar.admin.controller;

import com.beggar.admin.dto.RoomDetail;
import com.beggar.admin.dto.RoomListItem;
import com.beggar.admin.service.AdminRoomService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminRoomController {

    private final AdminRoomService adminRoomService;

    public AdminRoomController(AdminRoomService adminRoomService) {
        this.adminRoomService = adminRoomService;
    }

    @GetMapping("/admin/rooms")
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<RoomListItem> rooms = adminRoomService.getRooms(keyword, status, page);

        model.addAttribute("pageTitle", "방 관리");
        model.addAttribute("pageDescription", "방 정보를 검색하고 상세 운영 데이터를 확인해.");
        model.addAttribute("activeMenu", "rooms");
        model.addAttribute("rooms", rooms);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "rooms/list";
    }

    @GetMapping("/admin/rooms/{roomNo}")
    public String detail(@PathVariable Long roomNo, Model model) {
        RoomDetail room = adminRoomService.getRoomDetail(roomNo);

        model.addAttribute("pageTitle", "방 상세");
        model.addAttribute("pageDescription", "방 기본 정보와 예산/참여/영수증 요약을 확인해.");
        model.addAttribute("activeMenu", "rooms");
        model.addAttribute("room", room);
        return "rooms/detail";
    }

    @PostMapping("/admin/rooms/{roomNo}/end")
    public String endRoom(@PathVariable Long roomNo, RedirectAttributes redirectAttributes) {
        adminRoomService.endRoom(roomNo);
        redirectAttributes.addFlashAttribute("message", "방을 종료 처리했어.");
        return "redirect:/admin/rooms/%d".formatted(roomNo);
    }

    @PostMapping("/admin/rooms/{roomNo}/delete")
    public String deleteRoom(@PathVariable Long roomNo, RedirectAttributes redirectAttributes) {
        adminRoomService.deleteRoom(roomNo);
        redirectAttributes.addFlashAttribute("message", "방을 삭제 처리했어.");
        return "redirect:/admin/rooms/%d".formatted(roomNo);
    }
}
