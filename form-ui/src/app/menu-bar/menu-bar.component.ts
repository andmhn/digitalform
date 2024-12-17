import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { AvatarModule } from 'primeng/avatar';
import { BadgeModule } from 'primeng/badge';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { Menubar } from 'primeng/menubar';
import { Ripple } from 'primeng/ripple';
import { UserService } from '../auth.service';

@Component({
  selector: 'app-menu-bar',
  standalone: true,
  imports: [Menubar, ButtonModule, BadgeModule, AvatarModule, InputTextModule, Ripple, CommonModule, RouterModule],
  templateUrl: './menu-bar.component.html',
  styleUrl: './menu-bar.component.scss'
})
export class MenuBarComponent implements OnInit {
    isLoggedIn:boolean = false;
    items: MenuItem[] | undefined;
    
    userService = inject(UserService);
    
    ngOnInit() {
        this.items = [
            {
                label: 'Home',
                icon: 'pi pi-home',
                href: '/'
            },
            {
                label: 'Public Forms',
                icon: 'pi pi-home',
                href: '/public-forms'
            },
        ];
    }

    logout() {
        this.userService.logout();
    }
}
