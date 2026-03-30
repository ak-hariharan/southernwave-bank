/*
 * Public API Surface of shared
 */

export * from './lib/shared.module';   // <-- VERY IMPORTANT
export * from './lib/material/material.module';
export * from './lib/ui/shared-button/shared-button';
export * from './lib/ui/shared-footer/shared-footer';
export * from './lib/models/auth.models';
export * from './lib/services/auth.service';
export * from './lib/interceptors/auth.interceptor';
export * from './lib/guards/auth.guard';
export * from './lib/guards/role.guard';
