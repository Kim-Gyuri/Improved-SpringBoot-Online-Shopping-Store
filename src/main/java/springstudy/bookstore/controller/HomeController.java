package springstudy.bookstore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springstudy.bookstore.domain.dto.ItemFormDto;
import springstudy.bookstore.domain.dto.MainItemDto;
import springstudy.bookstore.domain.dto.ProductInfoDto;
import springstudy.bookstore.domain.dto.sort.ItemSearchCondition;
import springstudy.bookstore.domain.dto.sort.PageDto;
import springstudy.bookstore.domain.entity.User;
import springstudy.bookstore.service.FileService;
import springstudy.bookstore.service.ItemService;
import springstudy.bookstore.util.validation.argumentResolver.Login;

import java.net.MalformedURLException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/bookstore")
public class HomeController {

    private final ItemService itemService;
    private final FileService fileService;

    @GetMapping("/home")
    public String home(Model model, @Login User loginUser,
                       @PageableDefault(size = 4) Pageable pageable,
                       @RequestParam(required = false, name = "code") String code,
                       ItemSearchCondition condition) {

        Page<MainItemDto> results;
        PageDto pageDto;

        if (loginUser == null) {
            return "login/loginForm";
        }

        if (code == null) {
            results = itemService.searchPageSort(condition, pageable);
            pageDto = new PageDto(results.getTotalElements(), pageable);
        } else {
            results = itemService.itemPriceSort(code, pageable);
            pageDto = new PageDto(results.getTotalElements(), code, pageable);
        }

        for (MainItemDto result : results) {
            log.info("items info-itemImg={}", result.getImgName());

            String fullPath = fileService.getFullPath(result.getImgName());
            log.info("file path check={}", fullPath);
        }
        log.info(pageDto.toString());
        log.info("page sortParam ={}", pageDto.getSortParam());

        model.addAttribute("items", results.getContent());
        model.addAttribute("page", pageDto);
        model.addAttribute("condition", condition);

        return "shop/index";
    }

    @GetMapping("/products/{itemId}")
    public String showOne(@PathVariable Long itemId, Model model) {

        ItemFormDto product = itemService.getItemDetail(itemId);

        ProductInfoDto info = new ProductInfoDto();
        info.updateProductInfo(product.getItemName(), product.getPrice(), product.getQuantity(), product.getItemType(), product.getCategoryType(), product.getStatus(), product.getItemImgDtoList());

        model.addAttribute("product", info);
        return "product/productInfo";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        //file:/users/.../nameh8787bghh33.png 이 uuid 경로를 찾아가지고,
        //urlResource 찾는다.
        String fullPath = fileService.getFullPath(filename);
        log.info("file path check={}", fullPath);
        return new UrlResource("file:" + fileService.getFullPath(filename));
    }


}
